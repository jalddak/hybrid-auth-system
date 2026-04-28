package com.has.backend.domain.auth.repository;

import com.has.backend.common.config.property.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryRedis implements RefreshTokenRepository {


    private static final String KEY_PREFIX = "RT:";
    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    @Override
    public void save(String username, String identifier, String refreshToken) {
        String key = KEY_PREFIX + username + ":" + identifier;
        redisTemplate.opsForValue().set(key, refreshToken, jwtProperties.getRefreshTokenValidity(), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean exists(String username, String identifier, String refreshToken) {
        String key = KEY_PREFIX + username + ":" + identifier;
        String value = redisTemplate.opsForValue().get(key);
        return refreshToken.equals(value);
    }

    @Override
    public void delete(String username, String identifier) {
        String key = KEY_PREFIX + username + ":" + identifier;
        redisTemplate.delete(key);
    }

    @Override
    public void deleteAll(String username) {
        String pattern = KEY_PREFIX + username + ":*";
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                redisTemplate.unlink(cursor.next());
            }
        }
    }
}

