package com.scarlxrd.ecommerce.product.exception;

import java.util.UUID;

public class ProductOwnershipException extends RuntimeException {

    public ProductOwnershipException(UUID productId) {
        super("Seller does not own product: " + productId);
    }
}
