package com.learnai.service;

import com.learnai.dto.common.PageResponse;
import com.learnai.dto.market.ModelCreateRequest;
import com.learnai.dto.market.ModelDto;
import com.learnai.dto.market.OrderCreateRequest;
import com.learnai.dto.market.OrderDto;
import com.learnai.dto.market.OrderStatusUpdateRequest;
import com.learnai.entity.Download;
import com.learnai.entity.Model3D;
import com.learnai.entity.ModelCategory;
import com.learnai.entity.ModelOrder;
import com.learnai.entity.OrderItem;
import com.learnai.entity.enums.OrderStatus;
import com.learnai.exception.ApiException;
import com.learnai.repository.DownloadRepository;
import com.learnai.repository.Model3DRepository;
import com.learnai.repository.ModelCategoryRepository;
import com.learnai.repository.ModelOrderRepository;
import com.learnai.repository.OrderItemRepository;
import com.learnai.security.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 3D 模型商城：目录浏览、提交（待审核）、下载、订单（服务端计价 + 状态机）
 */
@Service
@RequiredArgsConstructor
public class MarketService {

    /** 管理员模拟发货的状态机（已支付后逐级推进） */
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.Pending, Set.of(OrderStatus.Processing),
            OrderStatus.Processing, Set.of(OrderStatus.Shipped),
            OrderStatus.Shipped, Set.of(OrderStatus.Completed));

    private final Model3DRepository modelRepository;
    private final ModelCategoryRepository categoryRepository;
    private final ModelOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DownloadRepository downloadRepository;
    private final FileStorageService fileStorage;

    // ---------- 模型目录 ----------

    @Transactional(readOnly = true)
    public PageResponse<ModelDto> listModels(Long categoryId, String search, int page, int size, String sort) {
        Specification<Model3D> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.isTrue(root.get("isApproved")));
            ps.add(cb.isTrue(root.get("isPublic")));
            if (categoryId != null) {
                ps.add(cb.equal(root.get("category").get("categoryId"), categoryId));
            }
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.add(cb.or(cb.like(root.get("modelName"), like), cb.like(root.get("creator"), like)));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        Sort order = switch (sort == null ? "" : sort) {
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> Sort.by(Sort.Direction.DESC, "createDate");
        };
        PageRequest pageable = PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, size), 50), order);
        return PageResponse.of(modelRepository.findAll(spec, pageable).map(ModelDto::from));
    }

    @Transactional(readOnly = true)
    public ModelDto modelDetail(Long id) {
        return ModelDto.from(findModelForRead(id));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> modelCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "categoryId"))
                .stream()
                .map(c -> Map.<String, Object>of(
                        "id", c.getCategoryId(),
                        "name", c.getCategoryName(),
                        "description", c.getDescription() == null ? "" : c.getDescription()))
                .toList();
    }

    /** 提交 3D 模型：进入待审核状态 */
    @Transactional
    public ModelDto createModel(ModelCreateRequest req, MultipartFile file) {
        ModelCategory category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> ApiException.badRequest("所选分类不存在"));
        String stored = fileStorage.storeModelFile(file);

        Model3D m = new Model3D();
        m.setModelName(req.getName());
        m.setCreator(req.getCreator());
        m.setCategory(category);
        m.setPrice(req.getPrice() == null ? BigDecimal.ZERO : req.getPrice());
        m.setPreviewUrl(req.getPreviewUrl());
        m.setIsPublic(req.getIsPublic() == null ? Boolean.TRUE : req.getIsPublic());
        m.setFilePath(stored);
        m.setOriginalFileName(file.getOriginalFilename());
        m.setIsApproved(false);
        m.setCreateDate(LocalDateTime.now());
        modelRepository.save(m);
        return ModelDto.from(m);
    }

    /** 下载模型文件（记录下载历史） */
    @Transactional
    public StoredFile downloadModel(Long userId, Long modelId, String ip) {
        Model3D m = findModelForRead(modelId);
        if (m.getFilePath() == null || m.getFilePath().isBlank()) {
            throw ApiException.badRequest("该模型暂无可下载文件");
        }
        Download d = new Download();
        d.setUserId(userId);
        d.setModelId(modelId);
        d.setIpAddress(ip);
        downloadRepository.save(d);
        return new StoredFile(fileStorage.load(m.getFilePath()),
                m.getOriginalFileName() != null && !m.getOriginalFileName().isBlank()
                        ? m.getOriginalFileName() : "model");
    }

    // ---------- 订单 ----------

    /** 创建订单：总价由服务端按模型现价计算 */
    @Transactional
    public OrderDto createOrder(Long userId, OrderCreateRequest req) {
        List<OrderCreateRequest.OrderItemRequest> items = req.items();
        List<Model3D> models = modelRepository.findAllById(
                items.stream().map(OrderCreateRequest.OrderItemRequest::modelId).toList());
        if (models.size() != items.size()) {
            throw ApiException.badRequest("订单中包含不存在的模型");
        }
        Map<Long, Model3D> byId = models.stream().collect(Collectors.toMap(Model3D::getModelId, x -> x));

        BigDecimal total = BigDecimal.ZERO;
        for (OrderCreateRequest.OrderItemRequest item : items) {
            Model3D m = byId.get(item.modelId());
            if (!Boolean.TRUE.equals(m.getIsApproved()) || !Boolean.TRUE.equals(m.getIsPublic())) {
                throw ApiException.badRequest("模型「" + m.getModelName() + "」暂不可购买");
            }
            int qty = item.quantity() == null ? 1 : item.quantity();
            total = total.add((m.getPrice() == null ? BigDecimal.ZERO : m.getPrice())
                    .multiply(BigDecimal.valueOf(qty)));
        }

        ModelOrder order = new ModelOrder();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setRecipientName(req.recipientName());
        order.setRecipientPhone(req.recipientPhone());
        order.setRecipientAddress(req.recipientAddress());
        order.setStatus(OrderStatus.PendingPayment);
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);

        for (OrderCreateRequest.OrderItemRequest item : items) {
            Model3D m = byId.get(item.modelId());
            int qty = item.quantity() == null ? 1 : item.quantity();
            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getOrderId());
            oi.setModelId(item.modelId());
            oi.setQuantity(qty);
            oi.setLicenseType(item.licenseType());
            oi.setUnitPrice(m.getPrice() == null ? BigDecimal.ZERO : m.getPrice());
            orderItemRepository.save(oi);
        }
        return toOrderDto(order, byId);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> myOrders(Long userId) {
        List<ModelOrder> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        return buildOrderDtos(orders);
    }

    @Transactional(readOnly = true)
    public OrderDto orderDetail(Long userId, Long orderId) {
        ModelOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("订单不存在"));
        if (!order.getUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw ApiException.notFound("订单不存在");
        }
        return toOrderDto(order, loadModelsForOrder(order));
    }

    /** 模拟支付：PendingPayment → Pending */
    @Transactional
    public OrderDto pay(Long userId, Long orderId) {
        ModelOrder order = mustOwn(userId, orderId);
        if (order.getStatus() != OrderStatus.PendingPayment) {
            throw ApiException.badRequest("当前订单状态不允许支付");
        }
        order.setStatus(OrderStatus.Pending);
        return toOrderDto(order, loadModelsForOrder(order));
    }

    /** 取消订单：PendingPayment / Pending → Cancelled */
    @Transactional
    public OrderDto cancel(Long userId, Long orderId) {
        ModelOrder order = mustOwn(userId, orderId);
        if (order.getStatus() != OrderStatus.PendingPayment && order.getStatus() != OrderStatus.Pending) {
            throw ApiException.badRequest("当前订单状态不允许取消");
        }
        order.setStatus(OrderStatus.Cancelled);
        return toOrderDto(order, loadModelsForOrder(order));
    }

    /** 管理员模拟发货状态机：Pending→Processing→Shipped→Completed */
    @Transactional
    public OrderDto updateStatus(Long orderId, OrderStatusUpdateRequest req) {
        OrderStatus target;
        try {
            target = OrderStatus.valueOf(req.status());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("无效的订单状态: " + req.status());
        }
        ModelOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("订单不存在"));
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.get(order.getStatus());
        if (allowed == null || !allowed.contains(target)) {
            throw ApiException.badRequest("订单状态不允许从 " + order.getStatus() + " 变更为 " + target);
        }
        order.setStatus(target);
        return toOrderDto(order, loadModelsForOrder(order));
    }

    // ---------- 内部工具 ----------

    private ModelOrder mustOwn(Long userId, Long orderId) {
        ModelOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("订单不存在"));
        if (!order.getUserId().equals(userId)) {
            throw ApiException.notFound("订单不存在");
        }
        return order;
    }

    private List<OrderDto> buildOrderDtos(List<ModelOrder> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }
        List<OrderItem> allItems = orderItemRepository.findByOrderIdIn(
                orders.stream().map(ModelOrder::getOrderId).toList());
        Map<Long, List<OrderItem>> byOrder = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));
        Map<Long, Model3D> models = modelRepository.findAllById(
                        allItems.stream().map(OrderItem::getModelId).distinct().toList())
                .stream().collect(Collectors.toMap(Model3D::getModelId, x -> x));
        return orders.stream()
                .map(o -> toOrderDto(o, byOrder.getOrDefault(o.getOrderId(), List.of()), models))
                .toList();
    }

    private Map<Long, Model3D> loadModelsForOrder(ModelOrder order) {
        List<OrderItem> items = orderItemRepository.findByOrderIdOrderByOrderItemIdAsc(order.getOrderId());
        return modelRepository.findAllById(items.stream().map(OrderItem::getModelId).distinct().toList())
                .stream().collect(Collectors.toMap(Model3D::getModelId, x -> x));
    }

    private OrderDto toOrderDto(ModelOrder order, Map<Long, Model3D> modelsById) {
        List<OrderItem> items = orderItemRepository.findByOrderIdOrderByOrderItemIdAsc(order.getOrderId());
        return toOrderDto(order, items, modelsById);
    }

    private OrderDto toOrderDto(ModelOrder order, List<OrderItem> items, Map<Long, Model3D> modelsById) {
        List<OrderDto.OrderItemDto> itemDtos = items.stream().map(oi -> {
            Model3D m = modelsById.get(oi.getModelId());
            BigDecimal unit = oi.getUnitPrice() == null ? BigDecimal.ZERO : oi.getUnitPrice();
            return new OrderDto.OrderItemDto(
                    oi.getOrderItemId(),
                    oi.getModelId(),
                    m == null ? null : m.getModelName(),
                    m == null ? null : m.getPreviewUrl(),
                    oi.getQuantity(),
                    oi.getLicenseType(),
                    unit,
                    unit.multiply(BigDecimal.valueOf(oi.getQuantity() == null ? 1 : oi.getQuantity())));
        }).toList();
        return new OrderDto(
                order.getOrderId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getRecipientName(),
                order.getRecipientPhone(),
                order.getRecipientAddress(),
                order.getStatus().name(),
                itemDtos);
    }

    private Model3D findModelForRead(Long id) {
        Model3D m = modelRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("模型不存在"));
        boolean visible = Boolean.TRUE.equals(m.getIsApproved()) && Boolean.TRUE.equals(m.getIsPublic());
        if (!visible && !SecurityUtils.isAdmin() && !SecurityUtils.isAuditor()) {
            throw ApiException.notFound("模型不存在");
        }
        return m;
    }

    /** 下载文件载体 */
    public record StoredFile(Resource resource, String originalName) {
    }
}
