package com.scarlxrd.ecommerce.order.exception;

import java.util.UUID;

public class DuplicateProductException extends RuntimeException {

    public DuplicateProductException(UUID productId) {
        super("Duplicate product in order: " + productId);
    }
}