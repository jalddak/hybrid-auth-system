package com.has.backend.domain.auth.repository;

import com.has.backend.common.config.property.EmailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class EmailVerifyRepositoryRedis implements EmailVerifyRepository {

    private final StringRedisTemplate redisTemplate;
    private final EmailProperties emailProperties;

    @Override
    public void saveVerificationCode(String prefix, String code) {
        String key = "verify:code:" + prefix;
        redisTemplate.opsForValue().set(key, code, emailProperties.getCode().getExpiredTime(), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean existsVerificationCode(String prefix, String code) {
        String key = "verify:code:" + prefix;
        String value = redisTemplate.opsForValue().get(key);
        if (code.equals(value)) return true;
        return false;
    }

    @Override
    public void deleteVerificationCode(String prefix) {
        String key = "verify:code:" + prefix;
        redisTemplate.delete(key);
    }

    @Override
    public void saveVerificationToken(String prefix, String token) {
        String key = "verify:token:" + prefix;
        redisTemplate.opsForValue().set(key, token, emailProperties.getToken().getExpiredTime(), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean existsVerificationToken(String prefix, String token) {
        String key = "verify:token:" + prefix;
        String value = redisTemplate.opsForValue().get(key);
        return token.equals(value);
    }

    @Override
    public void deleteVerificationToken(String prefix) {
        String key = "verify:token:" + prefix;
        redisTemplate.delete(key);
    }
}

