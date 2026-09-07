package com.scarlxrd.ecommerce.auth.config.security;

import com.scarlxrd.ecommerce.auth.service.redis.RedisService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class TokenBlacklistFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Inject
    RedisService redisService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (jwt == null || jwt.getTokenID() == null) {
            return;
        }

        String jti = jwt.getTokenID();

        if (redisService.isBlackListed(jti)) {
            requestContext.abortWith(
                    jakarta.ws.rs.core.Response
                            .status(jakarta.ws.rs.core.Response.Status.UNAUTHORIZED)
                            .entity("Token revoked")
                            .build()
            );
        }
    }
}