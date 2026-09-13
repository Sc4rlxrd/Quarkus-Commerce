package com.scarlxrd.ecommerce.contracts.product.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @Size(max = 180)
        String name,

        String description,

        @Size(max = 80)
        String sku,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal price,

        @Min(0)
        Integer stock,

        @Size(max = 100)
        String category,

        Boolean active
) {
}
