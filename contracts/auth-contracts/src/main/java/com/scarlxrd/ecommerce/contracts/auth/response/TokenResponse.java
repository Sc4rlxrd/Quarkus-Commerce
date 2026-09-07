package com.scarlxrd.ecommerce.contracts.auth.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}