package com.scarlxrd.ecommerce.order.entity;

public enum OrderStatus {
    PENDING,
    RESERVING_STOCK,
    STOCK_RESERVED,
    PAYMENT_PENDING,
    PAID,
    STOCK_FAILED,
    PAYMENT_FAILED,
    CANCELLED
}