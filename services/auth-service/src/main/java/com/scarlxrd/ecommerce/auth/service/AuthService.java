package com.scarlxrd.ecommerce.auth.service;

import com.scarlxrd.ecommerce.auth.config.security.TokenService;
import com.scarlxrd.ecommerce.auth.entity.UserEntity;
import com.scarlxrd.ecommerce.auth.enums.TokenType;
import com.scarlxrd.ecommerce.auth.enums.UserRole;
import com.scarlxrd.ecommerce.auth.exception.EmailAlreadyExistsException;
import com.scarlxrd.ecommerce.auth.exception.InvalidCredentialsException;
import com.scarlxrd.ecommerce.auth.exception.InvalidTokenException;
import com.scarlxrd.ecommerce.auth.repository.UserRepository;
import com.scarlxrd.ecommerce.auth.service.redis.RedisService;
import com.scarlxrd.ecommerce.contracts.auth.request.LoginRequest;
import com.scarlxrd.ecommerce.contracts.auth.request.RegisterRequest;
import com.scarlxrd.ecommerce.contracts.auth.response.TokenResponse;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;

@ApplicationScoped
public class AuthService {

    private final UserRepository repository;
    private final TokenService tokenService;
    private final RedisService redisService;

    public AuthService(UserRepository repository, TokenService tokenService, RedisService redisService) {
        this.repository = repository;
        this.tokenService = tokenService;
        this.redisService = redisService;
    }

    public TokenResponse login(LoginRequest data) {

        String email = normalizeEmail(data.email());

        UserEntity user = repository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        if (!BcryptUtil.matches(data.password(), user.passwordHash)) {
            throw new InvalidCredentialsException();
        }

        return generateAndStoreTokens(user);
    }

    @Transactional
    public void register(RegisterRequest data) {

        String email = normalizeEmail(data.email());

        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        UserEntity newUser = new UserEntity();

        newUser.name = data.name().trim();
        newUser.email = email;
        newUser.passwordHash = BcryptUtil.bcryptHash(data.password());

        newUser.roles.add(UserRole.USER);
        newUser.roles.add(UserRole.CUSTOMER);

        repository.persist(newUser);
    }

    public TokenResponse refreshToken(String refreshToken) {

        var decoded = tokenService.decode(refreshToken);

        String tokenType =
                decoded.getClaim("tokenType");

        if (!TokenType.REFRESH.name().equals(tokenType)) {
            throw new InvalidTokenException(
                    "Token is not a refresh token"
            );
        }

        String refreshJti = decoded.getTokenID();

        if (refreshJti == null ||
                !redisService.isRefreshTokenValid(refreshJti)) {

            throw new InvalidTokenException(
                    "Invalid Refresh Token"
            );
        }

        String email = decoded.getSubject();

        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidTokenException(
                                "User not found"
                        )
                );

        /*
         * Rotação do refresh token.
         *
         * Depois que ele foi utilizado,
         * não pode ser utilizado novamente.
         */
        redisService.deleteRefreshToken(refreshJti);

        return generateAndStoreTokens(user);
    }

    public void logout(String authHeader) {

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);

        if (tokenService.getTokenType(token)
                != TokenType.ACCESS) {

            throw new InvalidTokenException("Token is not an access token");
        }

        String jti = tokenService.getJti(token);

        long ttl =
                tokenService.getExpiration(token)
                        .getEpochSecond()
                        - Instant.now().getEpochSecond();

        if (ttl > 0) {
            redisService.blackListToken(jti, ttl);
        }
    }

    private TokenResponse generateAndStoreTokens(UserEntity user) {

        String accessToken = tokenService.generateAccessToken(user);

        String refreshToken = tokenService.generateRefreshToken(user);

        String refreshJti = tokenService.getJti(refreshToken);

        long ttl = tokenService.getExpiration(refreshToken)
                        .getEpochSecond()
                        - Instant.now().getEpochSecond();

        redisService.saveRefreshToken(
                refreshJti,
                ttl
        );

        return new TokenResponse(
                accessToken,
                refreshToken
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}