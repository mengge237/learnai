package com.learnai.dto.admin;

import com.learnai.dto.market.OrderDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员订单列表项（含下单用户与明细摘要）
 */
public record AdminOrderDto(
        Long id,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String username,
        String recipientName,
        String status,
        List<OrderDto.OrderItemDto> items
) {
}
