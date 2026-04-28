package com.has.backend.domain.auth.service;

import com.has.backend.common.exception.CustomException;
import com.has.backend.domain.auth.dto.EmailTokenDto;
import com.has.backend.domain.auth.enums.EmailVerifyPurpose;
import com.has.backend.domain.auth.repository.AccessTokenRepository;
import com.has.backend.domain.auth.repository.EmailVerifyRepository;
import com.has.backend.domain.auth.repository.RefreshTokenRepository;
import com.has.backend.domain.user.entity.User;
import com.has.backend.domain.user.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountRecoveryService {

    private final EmailVerifyRepository emailVerifyRepository;
    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public String findUsername(EmailTokenDto dto) {
        String email = dto.email();
        String token = dto.token();
        String prefix = EmailVerifyPurpose.FIND_ID.getPrefix() + ":" + email;

        User user = validateTokenAndUser(token, prefix, email);

        emailVerifyRepository.deleteVerificationToken(prefix);
        return user.getUsername();
    }

    public void resetPassword(EmailTokenDto dto) {
        String email = dto.email();
        String token = dto.token();
        String prefix = EmailVerifyPurpose.PASSWORD_RESET.getPrefix() + ":" + email;

        User user = validateTokenAndUser(token, prefix, email);

        user.updatePassword(passwordEncoder.encode(dto.password()));

        emailVerifyRepository.deleteVerificationToken(prefix);
        // JWT iat는 초 단위로 저장되므로 밀리초 부분을 버려 새 토큰이 무효화되지 않도록 함
        long changedAtMs = (System.currentTimeMillis() / 1000) * 1000;

        String username = user.getUsername();
        refreshTokenRepository.deleteAll(username);
        accessTokenRepository.saveInvalidationTime(username, changedAtMs);
    }

    private User validateTokenAndUser(String token, String prefix, String email) {
        if (!emailVerifyRepository.existsVerificationToken(prefix, token)) {
            log.error("Invalid or expired verification token. email={}", email);
            throw new CustomException(HttpStatus.BAD_REQUEST, "인증된 연결이 아닙니다.");
        }

        User user = userJpaRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.error("User not found. email={}", email);
            throw new CustomException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.");
        }

        return user;
    }
}
