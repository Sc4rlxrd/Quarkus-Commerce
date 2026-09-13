package com.scarlxrd.ecommerce.order.exception;

import java.util.UUID;

public class OrderOwnershipException extends RuntimeException {

    public OrderOwnershipException(UUID orderId) {
        super("Customer does not own order: " + orderId);
    }
}