package com.learnai.entity.enums;

/**
 * 订单状态（购物车状态已移除，购物车由前端 Pinia 管理）
 */
public enum OrderStatus {
    PendingPayment,
    Pending,
    Processing,
    Shipped,
    Completed,
    Cancelled
}
