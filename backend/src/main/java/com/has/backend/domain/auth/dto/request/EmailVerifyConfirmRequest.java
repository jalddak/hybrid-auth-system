package com.has.backend.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerifyConfirmRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String code
) {
}
