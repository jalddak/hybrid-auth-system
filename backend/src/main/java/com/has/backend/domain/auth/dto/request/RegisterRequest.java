package com.has.backend.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.has.backend.common.validation.AlphaNumeric;
import com.has.backend.common.validation.AlphaNumericSpecialCharOnly;
import com.has.backend.common.validation.PasswordComplexity;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(

        @NotBlank
        @AlphaNumeric
        String username,

        @NotBlank
        @AlphaNumericSpecialCharOnly
        @PasswordComplexity
        String password,

        @NotBlank
        String confirmPassword,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String token
) {
    @AssertTrue(message = "Passwords don't match")
    @JsonIgnore
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
