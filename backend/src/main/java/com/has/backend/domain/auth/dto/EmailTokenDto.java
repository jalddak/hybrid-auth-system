package com.has.backend.domain.auth.dto;

public record EmailTokenDto(
        String email,
        String token,
        String password
) {
}
