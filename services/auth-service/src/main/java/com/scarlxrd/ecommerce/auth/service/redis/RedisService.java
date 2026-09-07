package com.scarlxrd.ecommerce.auth.service.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RedisService {

    private static final Logger log = Logger.getLogger(RedisService.class);

    private static final String REFRESH_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String RATE_LIMIT_PREFIX = "rl:";

    private final ValueCommands<String, String> values;
    private final KeyCommands<String> keys;

    public RedisService(RedisDataSource redisDataSource) {
        this.values = redisDataSource.value(String.class);
        this.keys = redisDataSource.key();
    }

    public void saveRefreshToken(String jti, long ttlSeconds) {

        ttlSeconds = Math.max(ttlSeconds, 0);

        if (ttlSeconds == 0) {
            return;
        }

        values.setex(
                REFRESH_PREFIX + jti,
                ttlSeconds,
                "valid"
        );

        log.infof(
                "Refresh token stored in Redis jti=%s ttl_seconds=%d",
                jti,
                ttlSeconds
        );
    }

    public boolean isRefreshTokenValid(String jti) {

        boolean exists = keys.exists(REFRESH_PREFIX + jti);

        if (!exists) {
            log.warnf(
                    "Invalid or expired refresh token attempt jti=%s",
                    jti
            );
        }

        return exists;
    }

    public void deleteRefreshToken(String jti) {
        keys.del(REFRESH_PREFIX + jti);
    }

    public void blackListToken(String jti, long ttlSeconds) {

        ttlSeconds = Math.max(ttlSeconds, 0);

        if (ttlSeconds == 0) {
            return;
        }

        values.setex(
                BLACKLIST_PREFIX + jti,
                ttlSeconds,
                "revoked"
        );
    }

    public boolean isBlackListed(String jti) {

        boolean blocked = keys.exists(BLACKLIST_PREFIX + jti);

        if (blocked) {
            log.warnf(
                    "Revoked token access attempt jti=%s",
                    jti
            );
        }

        return blocked;
    }

    public boolean isAllowed(
            String key,
            int limit,
            long seconds
    ) {

        String redisKey = RATE_LIMIT_PREFIX + key;

        long current = values.incr(redisKey);

        if (current == 1) {
            keys.expire(redisKey, seconds);
        }

        boolean allowed = current <= limit;

        if (!allowed) {
            log.warnf(
                    "Rate limit exceeded key=%s",
                    key
            );
        }

        return allowed;
    }

    public Long getExpire(String key) {

        return keys.ttl(
                RATE_LIMIT_PREFIX + key
        );
    }
}