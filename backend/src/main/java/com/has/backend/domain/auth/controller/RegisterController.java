package com.has.backend.domain.auth.controller;

import com.has.backend.common.ApiResponse;
import com.has.backend.common.validation.AlphaNumeric;
import com.has.backend.domain.auth.dto.EmailVerifyConfirmDto;
import com.has.backend.domain.auth.dto.EmailVerifyDto;
import com.has.backend.domain.auth.dto.request.EmailVerifyConfirmRequest;
import com.has.backend.domain.auth.dto.request.EmailVerifyRequest;
import com.has.backend.domain.auth.dto.request.RegisterRequest;
import com.has.backend.domain.auth.dto.response.EmailVerifyConfirmResponse;
import com.has.backend.domain.auth.enums.EmailVerifyPurpose;
import com.has.backend.domain.auth.service.EmailService;
import com.has.backend.domain.auth.service.RegisterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/signup")
@Tag(name = "Auth - Register", description = "회원가입 관련 API")
public class RegisterController {

    private final EmailService emailService;
    private final RegisterService registerService;

    @PostMapping("/email-verifications")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@Valid @RequestBody EmailVerifyRequest request) {
        EmailVerifyDto dto = new EmailVerifyDto(EmailVerifyPurpose.REGISTER, request.email());
        emailService.sendVerificationCode(dto);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/email-verifications/confirm")
    public ResponseEntity<ApiResponse<EmailVerifyConfirmResponse>> verifyEmail(@Valid @RequestBody EmailVerifyConfirmRequest request) {
        EmailVerifyConfirmDto dto = new EmailVerifyConfirmDto(EmailVerifyPurpose.REGISTER, request.email(), request.code());
        EmailVerifyConfirmResponse response = emailService.verifyEmail(dto);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/username/{username}/available")
    public ResponseEntity<ApiResponse<Void>> checkUsernameAvailable(
            @PathVariable @Valid @NotBlank @AlphaNumeric String username) {
        registerService.checkUsername(username);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        registerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(null));
    }


}
