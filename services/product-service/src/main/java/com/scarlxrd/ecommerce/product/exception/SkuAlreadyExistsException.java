package com.scarlxrd.ecommerce.product.exception;

public class SkuAlreadyExistsException extends RuntimeException {

    public SkuAlreadyExistsException(String sku) {
        super("SKU already exists: " + sku);
    }
}
