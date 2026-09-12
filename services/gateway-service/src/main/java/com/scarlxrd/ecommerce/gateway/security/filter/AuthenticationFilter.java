package com.scarlxrd.ecommerce.gateway.security.filter;

import com.scarlxrd.ecommerce.gateway.security.GatewayAuthenticated;
import com.scarlxrd.ecommerce.gateway.security.TokenBlacklistService;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Set;

@Provider
@GatewayAuthenticated
@Priority(Priorities.AUTHORIZATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger log = Logger.getLogger(AuthenticationFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";

    private final JWTParser jwtParser;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationFilter(JWTParser jwtParser, TokenBlacklistService tokenBlacklistService) {
        this.jwtParser = jwtParser;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {

        String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorization == null || authorization.isBlank()) {
            abort(
                    requestContext,
                    "No Authorization Header"
            );
            return;
        }

        if (!authorization.startsWith(BEARER_PREFIX)) {
            abort(
                    requestContext,
                    "Invalid Authorization Header"
            );
            return;
        }

        String token = authorization.substring(BEARER_PREFIX.length());

        final JsonWebToken jwt;

        try {
            jwt = jwtParser.parse(token);
        } catch (Exception exception) {
            log.warn("Invalid JWT received by gateway");

            abort(
                    requestContext,
                    "Invalid Token"
            );
            return;
        }

        String tokenType = jwt.getClaim("tokenType");

        if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
            abort(
                    requestContext,
                    "Token is not an access token"
            );
            return;
        }

        String jti = jwt.getTokenID();

        if (tokenBlacklistService.isBlacklisted(jti)) {
            log.warnf(
                    "Revoked access token attempt jti=%s",
                    jti
            );

            abort(
                    requestContext,
                    "Token revoked"
            );
            return;
        }

        String userId = jwt.getClaim("userId");
        String email = jwt.getSubject();

        Set<String> roles = jwt.getGroups();

        String rolesHeader = roles == null ? "" : String.join(",", roles);

        requestContext.getHeaders().putSingle(
                "X-User-Id",
                userId
        );

        requestContext.getHeaders().putSingle(
                "X-User-Email",
                email
        );

        requestContext.getHeaders().putSingle(
                "X-User-Roles",
                rolesHeader
        );
    }

    private void abort(ContainerRequestContext requestContext, String detail) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(
                                Map.of(
                                        "status", 401,
                                        "title", "Unauthorized",
                                        "detail", detail
                                )
                        )
                        .build()
        );
    }
}