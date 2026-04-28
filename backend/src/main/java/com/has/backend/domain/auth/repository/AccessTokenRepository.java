package com.has.backend.domain.auth.repository;

import java.util.Optional;

public interface AccessTokenRepository {

    // 로그아웃 - 토큰 단위 블랙리스트
    void blacklist(String accessToken, long expiredInMs);

    boolean isBlacklisted(String accessToken);

    // 패스워드 변경 / 유저 삭제
    void saveInvalidationTime(String username, long invalidatedAtMs);

    Optional<Long> findInvalidationTime(String username);
}
