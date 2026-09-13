package com.scarlxrd.ecommerce.contracts.product.event;

import java.util.UUID;

public record ProductValidationRequest(
        UUID orderId,
        UUID productId,
        int quantity
) {
}
