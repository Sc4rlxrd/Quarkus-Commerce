package com.scarlxrd.ecommerce.auth.config.security;

import com.scarlxrd.ecommerce.auth.entity.UserEntity;
import com.scarlxrd.ecommerce.auth.enums.TokenType;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TokenService {

    private static final String ISSUER = "quarkus-commerce";

    private final JWTParser jwtParser;

    @ConfigProperty(name = "app.jwt.access-token-expiration")
    long accessTokenExpiration;

    @ConfigProperty(name = "app.jwt.refresh-token-expiration")
    long refreshTokenExpiration;

    public TokenService(JWTParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    public String generateAccessToken(UserEntity user) {

        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();

        Set<String> groups = user.roles.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return Jwt.issuer(ISSUER)
                .subject(user.email)
                .groups(groups)
                .claim("userId", user.id.toString())
                .claim("tokenType", TokenType.ACCESS.name())
                .claim("jti", jti)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpiration))
                .sign();
    }

    public String generateRefreshToken(UserEntity user) {

        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();

        return Jwt.issuer(ISSUER)
                .subject(user.email)
                .claim("userId", user.id.toString())
                .claim("tokenType", TokenType.REFRESH.name())
                .claim("jti", jti)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenExpiration))
                .sign();
    }

    public JsonWebToken decode(String token) {
        try {
            return jwtParser.parse(token);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid token", exception);
        }
    }

    public String getSubject(String token) {
        return decode(token).getSubject();
    }

    public String getJti(String token) {
        return decode(token).getTokenID();
    }

    public Instant getExpiration(String token) {
        return Instant.ofEpochSecond(
                decode(token).getExpirationTime()
        );
    }

    public TokenType getTokenType(String token) {

        String type = decode(token).getClaim("tokenType");

        return TokenType.valueOf(type);
    }
}