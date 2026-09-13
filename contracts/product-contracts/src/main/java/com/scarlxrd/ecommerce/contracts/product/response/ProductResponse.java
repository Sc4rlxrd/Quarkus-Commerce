package com.scarlxrd.ecommerce.contracts.product.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        String sku,
        BigDecimal price,
        Integer stock,
        UUID sellerId,
        String category,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}