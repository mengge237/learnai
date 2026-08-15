package com.learnai.dto.audit;

/**
 * 审核工作台统计
 */
public record AuditStatsDto(
        long pendingResources,
        long pendingModels,
        long totalReviewed,
        long reviewedToday
) {
}
