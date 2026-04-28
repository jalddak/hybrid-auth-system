package com.has.backend.domain.auth.repository;

public interface RefreshTokenRepository {

    void save(String username, String identifier, String refreshToken);

    boolean exists(String username, String identifier, String refreshToken);

    void delete(String username, String identifier);

    void deleteAll(String username);
}
