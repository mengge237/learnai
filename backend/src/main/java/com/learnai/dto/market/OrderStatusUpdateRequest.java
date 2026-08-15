package com.learnai.dto.market;

import jakarta.validation.constraints.NotBlank;

/**
 * 管理员更新订单状态请求（模拟发货状态机）
 */
public record OrderStatusUpdateRequest(
        @NotBlank(message = "状态不能为空")
        String status
) {
}
