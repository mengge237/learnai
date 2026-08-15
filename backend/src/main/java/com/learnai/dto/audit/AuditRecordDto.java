package com.learnai.dto.audit;

import java.time.LocalDateTime;

/**
 * 审核历史记录项
 */
public record AuditRecordDto(
        Long id,
        String type,        // resource / model
        String title,
        String author,
        String result,      // approved / rejected
        String reason,      // 驳回原因（通过时为空）
        String reviewerName,
        LocalDateTime reviewedAt
) {
}
