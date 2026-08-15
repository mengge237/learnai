package com.learnai.dto.audit;

import jakarta.validation.constraints.NotNull;

/**
 * 审核请求（approved=true 通过；false 驳回并附原因）
 */
public record AuditRequest(
        @NotNull(message = "审核结果不能为空")
        Boolean approved,
        String reason
) {
}
