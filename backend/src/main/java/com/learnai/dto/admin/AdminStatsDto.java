package com.learnai.dto.admin;

import java.math.BigDecimal;

/**
 * 管理端统计总览
 */
public record AdminStatsDto(
        long userCount,
        long resourceCount,
        long pendingResourceCount,
        long modelCount,
        long pendingModelCount,
        long orderCount,
        long pendingOrderCount,
        long commentCount,
        long favoriteCount,
        long downloadCount,
        long completedOrderCount,
        BigDecimal totalSalesAmount
) {
}
