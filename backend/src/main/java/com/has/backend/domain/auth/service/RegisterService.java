package com.has.backend.domain.auth.service;

import com.has.backend.common.exception.CustomException;
import com.has.backend.domain.auth.dto.request.RegisterRequest;
import com.has.backend.domain.auth.enums.EmailVerifyPurpose;
import com.has.backend.domain.auth.repository.EmailVerifyRepository;
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
public class RegisterService {

    private final EmailVerifyRepository emailVerifyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository userJpaRepository;

    public void checkUsername(String username) {
        if (userJpaRepository.existsByUsername(username)) {
            log.error("Username already in use. username={}", username);
            throw new CustomException(HttpStatus.CONFLICT, "중복된 아이디 입니다.");
        }
    }

    public void register(RegisterRequest request) {
        String email = request.email();
        String prefix = EmailVerifyPurpose.REGISTER.getPrefix() + ":" + email;

        if (!emailVerifyRepository.existsVerificationToken(prefix, request.token())) {
            log.error("Invalid or expired verification token. email={}", email);
            throw new CustomException(HttpStatus.BAD_REQUEST, "인증된 연결이 아닙니다.");
        }

        String username = request.username();
        validateUser(username, email);

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .build();

        userJpaRepository.save(user);
        emailVerifyRepository.deleteVerificationToken(prefix);
    }

    private void validateUser(String username, String email) {
        if (userJpaRepository.existsByUsername(username)) {
            log.error("Username already in use. username={}", username);
            throw new CustomException(HttpStatus.CONFLICT, "중복된 아이디 입니다.");
        }

        if (userJpaRepository.existsByEmail(email)) {
            log.error("Email already in use. email={}", email);
            throw new CustomException(HttpStatus.CONFLICT, "중복된 이메일 입니다.");
        }
    }
}
