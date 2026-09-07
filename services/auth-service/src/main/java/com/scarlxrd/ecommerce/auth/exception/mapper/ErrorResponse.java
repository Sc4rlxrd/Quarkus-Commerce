package com.scarlxrd.ecommerce.auth.exception.mapper;

public record ErrorResponse(
        int status,
        String title,
        String detail
) {
}