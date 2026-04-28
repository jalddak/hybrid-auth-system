package com.has.backend.domain.auth.dto;

import com.has.backend.domain.auth.enums.EmailVerifyPurpose;

public record EmailVerifyConfirmDto(
        EmailVerifyPurpose purpose,
        String email,
        String code
) {
}
