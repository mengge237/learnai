package com.learnai.dto.market;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 创建订单请求（总价由服务端按模型价格计算，客户端金额一律忽略）
 */
public record OrderCreateRequest(
        @NotEmpty(message = "订单商品不能为空")
        List<@Valid OrderItemRequest> items,
        @NotBlank(message = "收货人不能为空")
        String recipientName,
        @NotBlank(message = "联系电话不能为空")
        String recipientPhone,
        @NotBlank(message = "收货地址不能为空")
        String recipientAddress
) {
    public record OrderItemRequest(
            @NotNull(message = "模型 ID 不能为空")
            Long modelId,
            @Min(value = 1, message = "数量至少为 1")
            Integer quantity,
            String licenseType
    ) {
    }
}
