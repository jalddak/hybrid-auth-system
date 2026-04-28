package com.has.backend.domain.user.controller;

import com.has.backend.common.ApiResponse;
import com.has.backend.common.config.property.CookieProperties;
import com.has.backend.domain.auth.dto.JwtDto;
import com.has.backend.domain.auth.dto.response.JwtResponse;
import com.has.backend.domain.auth.security.CustomUserDetails;
import com.has.backend.domain.auth.util.CookieUtil;
import com.has.backend.domain.user.dto.request.ChangePasswordRequest;
import com.has.backend.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "유저 관련 API")
public class UserController {

    private final UserService userService;
    private final CookieProperties cookieProperties;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Void>> verifyToken() {
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<JwtResponse>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request, HttpServletResponse response
    ) {

        JwtDto jwtDto = userService.changePassword(userDetails.getUserId(), request);

        response.setHeader("Authorization", "Bearer " + jwtDto.accessToken());
        response.addCookie(CookieUtil.createCookie("refresh", jwtDto.refreshToken(), cookieProperties.getMaxAge()));

        return ResponseEntity.ok(ApiResponse.ok(new JwtResponse(jwtDto.accessToken())));
    }
}
