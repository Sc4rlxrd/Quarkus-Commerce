package com.scarlxrd.ecommerce.contracts.product.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank
        @Size(max = 180)
        String name,

        String description,

        @NotBlank
        @Size(max = 80)
        String sku,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stock,

        @Size(max = 100)
        String category
) {
}