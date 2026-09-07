package com.scarlxrd.ecommerce.auth.config.security;

import jakarta.enterprise.context.RequestScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Set;
import java.util.UUID;

@RequestScoped
public class AuthenticatedUser {

    private final JsonWebToken jwt;

    public AuthenticatedUser(JsonWebToken jwt) {
        this.jwt = jwt;
    }

    public UUID getUserId() {
        return UUID.fromString(jwt.getClaim("userId"));
    }

    public String getEmail() {
        return jwt.getSubject();
    }

    public String getJti() {
        return jwt.getTokenID();
    }

    public long getExpiration() {
        return jwt.getExpirationTime();
    }

    public Set<String> getRoles() {
        return jwt.getGroups();
    }
}