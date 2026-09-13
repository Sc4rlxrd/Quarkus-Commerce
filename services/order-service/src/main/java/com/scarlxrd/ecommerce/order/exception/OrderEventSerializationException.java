package com.scarlxrd.ecommerce.order.exception;

public class OrderEventSerializationException extends RuntimeException {

    public OrderEventSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}