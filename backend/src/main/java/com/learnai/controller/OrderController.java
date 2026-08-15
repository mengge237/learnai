package com.learnai.controller;

import com.learnai.dto.market.OrderCreateRequest;
import com.learnai.dto.market.OrderDto;
import com.learnai.dto.market.OrderStatusUpdateRequest;
import com.learnai.security.SecurityUtils;
import com.learnai.service.MarketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单：创建（服务端计价）、我的订单、模拟支付/取消、管理员发货状态机
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final MarketService marketService;

    @PostMapping
    public OrderDto create(@Valid @RequestBody OrderCreateRequest req) {
        return marketService.createOrder(SecurityUtils.currentUserId(), req);
    }

    @GetMapping("/my")
    public List<OrderDto> my() {
        return marketService.myOrders(SecurityUtils.currentUserId());
    }

    @GetMapping("/{id}")
    public OrderDto detail(@PathVariable Long id) {
        return marketService.orderDetail(SecurityUtils.currentUserId(), id);
    }

    /** 模拟支付：PendingPayment → Pending */
    @PostMapping("/{id}/pay")
    public OrderDto pay(@PathVariable Long id) {
        return marketService.pay(SecurityUtils.currentUserId(), id);
    }

    @PostMapping("/{id}/cancel")
    public OrderDto cancel(@PathVariable Long id) {
        return marketService.cancel(SecurityUtils.currentUserId(), id);
    }

    /** 管理员模拟发货：Pending → Processing → Shipped → Completed */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDto updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest req) {
        return marketService.updateStatus(id, req);
    }
}
