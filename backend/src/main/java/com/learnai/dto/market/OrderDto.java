package com.learnai.dto.market;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情（含明细）
 */
public record OrderDto(
        Long id,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String recipientName,
        String recipientPhone,
        String recipientAddress,
        String status,
        List<OrderItemDto> items
) {
    public record OrderItemDto(
            Long orderItemId,
            Long modelId,
            String modelName,
            String previewUrl,
            Integer quantity,
            String licenseType,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {
    }
}
