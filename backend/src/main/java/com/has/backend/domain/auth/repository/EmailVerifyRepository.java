package com.has.backend.domain.auth.repository;

public interface EmailVerifyRepository {

    void saveVerificationCode(String prefix, String code);

    boolean existsVerificationCode(String prefix, String code);

    void deleteVerificationCode(String prefix);

    void saveVerificationToken(String prefix, String token);

    boolean existsVerificationToken(String prefix, String token);

    void deleteVerificationToken(String prefix);
}
