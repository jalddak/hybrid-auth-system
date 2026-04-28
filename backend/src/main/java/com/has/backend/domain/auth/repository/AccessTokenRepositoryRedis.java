package com.has.backend.domain.auth.repository;

import com.has.backend.common.config.property.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AccessTokenRepositoryRedis implements AccessTokenRepository {

    private static final String LOGOUT_PREFIX = "AC:logout:";
    private static final String EXPIRED_PREFIX = "AC:expired:";

    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    @Override
    public void blacklist(String accessToken, long expiredInMs) {
        redisTemplate.opsForValue().set(LOGOUT_PREFIX + accessToken, "logout", expiredInMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(LOGOUT_PREFIX + accessToken);
    }

    @Override
    public void saveInvalidationTime(String username, long invalidatedAtMs) {
        redisTemplate.opsForValue().set(EXPIRED_PREFIX + username, String.valueOf(invalidatedAtMs),
                jwtProperties.getAccessTokenValidity(), TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<Long> findInvalidationTime(String username) {
        String value = redisTemplate.opsForValue().get(EXPIRED_PREFIX + username);
        return Optional.ofNullable(value).map(Long::parseLong);
    }
}
