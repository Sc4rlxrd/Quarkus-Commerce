package com.scarlxrd.ecommerce.gateway.security;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final KeyCommands<String> keys;

    public TokenBlacklistService(RedisDataSource redisDataSource) {
        this.keys = redisDataSource.key();
    }

    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) {
            return true;
        }

        return keys.exists(BLACKLIST_PREFIX + jti);
    }
}