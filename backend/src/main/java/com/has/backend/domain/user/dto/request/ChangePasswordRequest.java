package com.has.backend.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.has.backend.common.validation.AlphaNumericSpecialCharOnly;
import com.has.backend.common.validation.PasswordComplexity;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(

        @NotBlank
        @AlphaNumericSpecialCharOnly
        @PasswordComplexity
        String password,

        @NotBlank
        @AlphaNumericSpecialCharOnly
        @PasswordComplexity
        String newPassword,

        @NotBlank
        String confirmPassword
) {
    @AssertTrue(message = "Passwords don't match")
    @JsonIgnore
    public boolean isPasswordConfirmed() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }

    @AssertTrue(message = "이전 비밀번호와 다르게 입력해주십시오.")
    @JsonIgnore
    public boolean isNewPasswordDifferentFromCurrent() {
        return newPassword != null && !newPassword.equals(password);
    }
}
