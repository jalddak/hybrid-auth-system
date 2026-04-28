package com.has.backend.domain.auth.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.has.backend.common.config.property.EmailProperties;
import com.has.backend.common.exception.CustomException;
import com.has.backend.common.util.MailClient;
import com.has.backend.domain.auth.dto.EmailVerifyConfirmDto;
import com.has.backend.domain.auth.dto.EmailVerifyDto;
import com.has.backend.domain.auth.dto.response.EmailVerifyConfirmResponse;
import com.has.backend.domain.auth.enums.EmailVerifyPurpose;
import com.has.backend.domain.auth.repository.EmailVerifyRepository;
import com.has.backend.domain.user.entity.User;
import com.has.backend.domain.user.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {
    private final UserJpaRepository userJpaRepository;
    private final EmailVerifyRepository emailVerifyRepository;
    private final TemplateEngine templateEngine;
    private final MailClient mailClient;
    private final EmailProperties emailProperties;

    public void sendVerificationCode(EmailVerifyDto dto) {

        String email = dto.email();
        EmailVerifyPurpose purpose = dto.purpose();
        validateUser(email, purpose);

        char[] availableChar = new char[]{'2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String code = NanoIdUtils.randomNanoId(new SecureRandom(), availableChar, 6);

        String prefix = purpose.getPrefix() + ":" + email;
        emailVerifyRepository.saveVerificationCode(prefix, code);

        String subject = purpose.getSubject();

        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("expiredTime", emailProperties.getCode().getExpiredTime() / (1000 * 60));
        String htmlContent = templateEngine.process("mail-auth", context);
        mailClient.sendEmail(email, subject, htmlContent);
    }

    private void validateUser(String email, EmailVerifyPurpose purpose) {
        User user = userJpaRepository.findByEmail(email).orElse(null);

        if (purpose == EmailVerifyPurpose.REGISTER) {
            if (user != null) {
                log.error("Email already in use. email={}", email);
                throw new CustomException(HttpStatus.CONFLICT, "이미 가입한 이메일입니다.");
            }
        } else {
            if (user == null) {
                log.error("Email not found. email={}", email);
                throw new CustomException(HttpStatus.NOT_FOUND, "가입되지 않은 이메일입니다.");
            }
        }
    }

    public EmailVerifyConfirmResponse verifyEmail(EmailVerifyConfirmDto dto) {

        String email = dto.email();
        String prefix = dto.purpose().getPrefix() + ":" + email;

        if (!emailVerifyRepository.existsVerificationCode(prefix, dto.code())) {
            log.error("Invalid or expired verification code. email={}", email);
            throw new CustomException(HttpStatus.BAD_REQUEST, "인증 코드가 틀렸습니다.");
        }

        emailVerifyRepository.deleteVerificationCode(prefix);

        String token = NanoIdUtils.randomNanoId();
        emailVerifyRepository.saveVerificationToken(prefix, token);

        return new EmailVerifyConfirmResponse(token);
    }
}
