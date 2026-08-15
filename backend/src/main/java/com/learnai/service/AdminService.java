package com.learnai.service;

import com.learnai.dto.admin.AdminOrderDto;
import com.learnai.dto.admin.AdminStatsDto;
import com.learnai.dto.admin.AdminUserDto;
import com.learnai.dto.admin.AdminUserUpdateRequest;
import com.learnai.dto.common.PageResponse;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.dto.market.ModelDto;
import com.learnai.dto.market.OrderDto;
import com.learnai.entity.LearningResource;
import com.learnai.entity.Model3D;
import com.learnai.entity.ModelOrder;
import com.learnai.entity.OrderItem;
import com.learnai.entity.User;
import com.learnai.entity.UserRole;
import com.learnai.entity.enums.OrderStatus;
import com.learnai.exception.ApiException;
import com.learnai.repository.CommentRepository;
import com.learnai.repository.DownloadRepository;
import com.learnai.repository.FavoriteRepository;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.Model3DRepository;
import com.learnai.repository.ModelOrderRepository;
import com.learnai.repository.OrderItemRepository;
import com.learnai.repository.UserRepository;
import com.learnai.repository.UserRoleRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员：用户管理、平台统计
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final LearningResourceRepository resourceRepository;
    private final Model3DRepository modelRepository;
    private final ModelOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final DownloadRepository downloadRepository;

    @Transactional(readOnly = true)
    public PageResponse<AdminUserDto> listUsers(String search, int page, int size) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.add(cb.or(
                        cb.like(root.get("username"), like),
                        cb.like(root.get("email"), like),
                        cb.like(root.get("phone"), like)));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        Page<User> result = userRepository.findAll(spec,
                PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, size), 50)));
        return PageResponse.of(result.map(AdminUserDto::from));
    }

    /** 修改用户角色/启用状态（禁止对自己降权或禁用，防止锁死系统） */
    @Transactional
    public AdminUserDto updateUser(Long currentUserId, Long targetId, AdminUserUpdateRequest req) {
        User user = userRepository.findById(targetId)
                .orElseThrow(() -> ApiException.notFound("用户不存在"));
        if (targetId.equals(currentUserId)) {
            throw ApiException.badRequest("不能修改自己的角色或状态");
        }
        if (req.roleId() != null) {
            UserRole role = userRoleRepository.findById(req.roleId())
                    .orElseThrow(() -> ApiException.badRequest("角色不存在"));
            user.setRole(role);
        }
        if (req.isActive() != null) {
            user.setIsActive(req.isActive());
        }
        return AdminUserDto.from(user);
    }

    @Transactional(readOnly = true)
    public AdminStatsDto stats() {
        long completedOrders = orderRepository.countByStatus(OrderStatus.Completed);
        return new AdminStatsDto(
                userRepository.count(),
                resourceRepository.count(),
                resourceRepository.countByIsApprovedFalseAndRejectionReasonIsNull(),
                modelRepository.count(),
                modelRepository.countByIsApprovedFalseAndRejectionReasonIsNull(),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.Pending),
                commentRepository.count(),
                favoriteRepository.count(),
                downloadRepository.count(),
                completedOrders,
                orderRepository.sumCompletedAmount());
    }

    // ---------- 资源管理 ----------

    /** 全部资源列表（status: all / pending / approved / rejected，标题模糊搜索） */
    @Transactional(readOnly = true)
    public PageResponse<ResourceDto> listResources(String search, String status, int page, int size) {
        Specification<LearningResource> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (search != null && !search.isBlank()) {
                ps.add(cb.like(cb.lower(root.get("resourceTitle")), "%" + search.trim().toLowerCase() + "%"));
            }
            if ("pending".equals(status)) {
                ps.add(cb.isFalse(root.get("isApproved")));
                ps.add(cb.isNull(root.get("rejectionReason")));
            } else if ("rejected".equals(status)) {
                ps.add(cb.isNotNull(root.get("rejectionReason")));
            } else if ("approved".equals(status)) {
                ps.add(cb.isTrue(root.get("isApproved")));
            }
            if (query != null) {
                query.orderBy(cb.desc(root.get("createDate")));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        Page<LearningResource> result = resourceRepository.findAll(spec,
                PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, size), 50)));
        return PageResponse.of(result.map(ResourceDto::from));
    }

    /** 上架 / 下架资源 */
    @Transactional
    public ResourceDto toggleResourcePublic(Long id, boolean isPublic) {
        LearningResource r = resourceRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("资源不存在"));
        r.setIsPublic(isPublic);
        return ResourceDto.from(r);
    }

    /** 删除资源（存在关联数据时拒绝并提示改用下架） */
    @Transactional
    public void deleteResource(Long id) {
        LearningResource r = resourceRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("资源不存在"));
        try {
            resourceRepository.delete(r);
            resourceRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw ApiException.badRequest("该资源存在关联数据（学习记录/评论/收藏等），无法删除，可改为下架");
        }
    }

    // ---------- 模型管理 ----------

    /** 全部模型列表（status: all / pending / approved / rejected，名称模糊搜索） */
    @Transactional(readOnly = true)
    public PageResponse<ModelDto> listModels(String search, String status, int page, int size) {
        Specification<Model3D> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (search != null && !search.isBlank()) {
                ps.add(cb.like(cb.lower(root.get("modelName")), "%" + search.trim().toLowerCase() + "%"));
            }
            if ("pending".equals(status)) {
                ps.add(cb.isFalse(root.get("isApproved")));
                ps.add(cb.isNull(root.get("rejectionReason")));
            } else if ("rejected".equals(status)) {
                ps.add(cb.isNotNull(root.get("rejectionReason")));
            } else if ("approved".equals(status)) {
                ps.add(cb.isTrue(root.get("isApproved")));
            }
            if (query != null) {
                query.orderBy(cb.desc(root.get("createDate")));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        Page<Model3D> result = modelRepository.findAll(spec,
                PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, size), 50)));
        return PageResponse.of(result.map(ModelDto::from));
    }

    /** 上架 / 下架模型 */
    @Transactional
    public ModelDto toggleModelPublic(Long id, boolean isPublic) {
        Model3D m = modelRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("模型不存在"));
        m.setIsPublic(isPublic);
        return ModelDto.from(m);
    }

    /** 删除模型（存在关联数据时拒绝并提示改用下架） */
    @Transactional
    public void deleteModel(Long id) {
        Model3D m = modelRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("模型不存在"));
        try {
            modelRepository.delete(m);
            modelRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw ApiException.badRequest("该模型存在关联数据（订单/收藏等），无法删除，可改为下架");
        }
    }

    // ---------- 订单管理 ----------

    /** 全部订单列表（可按状态筛选，含下单用户与明细） */
    @Transactional(readOnly = true)
    public PageResponse<AdminOrderDto> listOrders(String status, int page, int size) {
        Specification<ModelOrder> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (status != null && !status.isBlank()) {
                OrderStatus target;
                try {
                    target = OrderStatus.valueOf(status);
                } catch (IllegalArgumentException e) {
                    throw ApiException.badRequest("无效的订单状态: " + status);
                }
                ps.add(cb.equal(root.get("status"), target));
            }
            if (query != null) {
                query.orderBy(cb.desc(root.get("orderDate")));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        Page<ModelOrder> result = orderRepository.findAll(spec,
                PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, size), 50)));
        List<ModelOrder> orders = result.getContent();
        Map<Long, String> users = orders.isEmpty() ? Map.of()
                : userRepository.findAllById(orders.stream().map(ModelOrder::getUserId).distinct().toList()).stream()
                        .collect(Collectors.toMap(User::getUserId, User::getUsername));
        List<OrderItem> items = orders.isEmpty() ? List.of()
                : orderItemRepository.findByOrderIdIn(orders.stream().map(ModelOrder::getOrderId).toList());
        Map<Long, List<OrderItem>> byOrder = items.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
        Map<Long, Model3D> models = modelRepository.findAllById(
                        items.stream().map(OrderItem::getModelId).distinct().toList()).stream()
                .collect(Collectors.toMap(Model3D::getModelId, x -> x));
        return PageResponse.of(result.map(o -> new AdminOrderDto(
                o.getOrderId(),
                o.getOrderDate(),
                o.getTotalAmount(),
                users.getOrDefault(o.getUserId(), "用户" + o.getUserId()),
                o.getRecipientName(),
                o.getStatus().name(),
                byOrder.getOrDefault(o.getOrderId(), List.of()).stream()
                        .map(i -> new OrderDto.OrderItemDto(
                                i.getOrderItemId(),
                                i.getModelId(),
                                models.containsKey(i.getModelId()) ? models.get(i.getModelId()).getModelName() : "已删除模型",
                                models.containsKey(i.getModelId()) ? models.get(i.getModelId()).getPreviewUrl() : null,
                                i.getQuantity(),
                                i.getLicenseType(),
                                i.getUnitPrice(),
                                i.getUnitPrice() == null || i.getQuantity() == null ? BigDecimal.ZERO
                                        : i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))))
                        .toList())));
    }
}
