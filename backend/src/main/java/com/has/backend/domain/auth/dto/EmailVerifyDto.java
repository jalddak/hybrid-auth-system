package com.has.backend.domain.auth.dto;

import com.has.backend.domain.auth.enums.EmailVerifyPurpose;

public record EmailVerifyDto(
        EmailVerifyPurpose purpose,
        String email
) {
}
