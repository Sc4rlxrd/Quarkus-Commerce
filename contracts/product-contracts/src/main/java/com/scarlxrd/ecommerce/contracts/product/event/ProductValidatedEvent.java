package com.scarlxrd.ecommerce.contracts.product.event;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductValidatedEvent(
        UUID orderId,
        UUID productId,
        int quantity,
        BigDecimal unitPrice,
        boolean available,
        String reason
) {
}
