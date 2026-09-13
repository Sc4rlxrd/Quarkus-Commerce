package com.scarlxrd.ecommerce.order.exception;

import java.util.UUID;

public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(UUID orderId, String currentStatus, String expectedStatus) {
        super("Order " + orderId + " is in status " + currentStatus + ", expected " + expectedStatus);
    }
}