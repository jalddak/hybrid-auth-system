package com.has.backend.domain.auth.dto;

public record JwtDto(

        String accessToken,
        String refreshToken
) {
}
