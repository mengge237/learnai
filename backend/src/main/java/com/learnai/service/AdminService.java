package com.learnai.service;

import com.learnai.dto.admin.AdminStatsDto;
import com.learnai.dto.admin.AdminUserDto;
import com.learnai.dto.admin.AdminUserUpdateRequest;
import com.learnai.dto.common.PageResponse;
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
import com.learnai.repository.UserRepository;
import com.learnai.repository.UserRoleRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
}
