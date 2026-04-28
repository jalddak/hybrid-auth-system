package com.has.backend.domain.auth.controller;

import com.has.backend.common.ApiResponse;
import com.has.backend.domain.auth.dto.EmailTokenDto;
import com.has.backend.domain.auth.dto.EmailVerifyConfirmDto;
import com.has.backend.domain.auth.dto.EmailVerifyDto;
import com.has.backend.domain.auth.dto.request.EmailVerifyConfirmRequest;
import com.has.backend.domain.auth.dto.request.EmailVerifyRequest;
import com.has.backend.domain.auth.dto.request.FindUsernameRequest;
import com.has.backend.domain.auth.dto.request.ResetPasswordRequest;
import com.has.backend.domain.auth.dto.response.EmailVerifyConfirmResponse;
import com.has.backend.domain.auth.dto.response.FindUsernameResponse;
import com.has.backend.domain.auth.enums.EmailVerifyPurpose;
import com.has.backend.domain.auth.service.AccountRecoveryService;
import com.has.backend.domain.auth.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/account-recovery")
@Tag(name = "Auth - Account Recovery", description = "아이디찾기, 비밀번호 초기화 API")
public class AccountRecoveryController {

    private final EmailService emailService;
    private final AccountRecoveryService accountRecoveryService;

    @PostMapping("/username/email-verifications")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCodeForUsername(@Valid @RequestBody EmailVerifyRequest request) {
        EmailVerifyDto dto = new EmailVerifyDto(EmailVerifyPurpose.FIND_ID, request.email());
        emailService.sendVerificationCode(dto);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/username/email-verifications/confirm")
    public ResponseEntity<ApiResponse<EmailVerifyConfirmResponse>> verifyEmailForUsername(@Valid @RequestBody EmailVerifyConfirmRequest request) {
        EmailVerifyConfirmDto dto = new EmailVerifyConfirmDto(EmailVerifyPurpose.FIND_ID, request.email(), request.code());
        EmailVerifyConfirmResponse response = emailService.verifyEmail(dto);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/username")
    public ResponseEntity<ApiResponse<FindUsernameResponse>> findUsername(
            @Valid @RequestBody FindUsernameRequest request) {
        String username = accountRecoveryService.findUsername(new EmailTokenDto(request.email(), request.token(), null));
        return ResponseEntity.ok(ApiResponse.ok(new FindUsernameResponse(username)));
    }

    @PostMapping("/password/email-verifications")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCodeForPassword(@Valid @RequestBody EmailVerifyRequest request) {
        EmailVerifyDto dto = new EmailVerifyDto(EmailVerifyPurpose.PASSWORD_RESET, request.email());
        emailService.sendVerificationCode(dto);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/password/email-verifications/confirm")
    public ResponseEntity<ApiResponse<EmailVerifyConfirmResponse>> verifyEmailForPassword(@Valid @RequestBody EmailVerifyConfirmRequest request) {
        EmailVerifyConfirmDto dto = new EmailVerifyConfirmDto(EmailVerifyPurpose.PASSWORD_RESET, request.email(), request.code());
        EmailVerifyConfirmResponse response = emailService.verifyEmail(dto);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        accountRecoveryService.resetPassword(new EmailTokenDto(request.email(), request.token(), request.password()));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
