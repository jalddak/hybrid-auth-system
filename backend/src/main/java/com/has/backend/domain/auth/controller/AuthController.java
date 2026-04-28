package com.has.backend.domain.auth.controller;

import com.has.backend.common.ApiResponse;
import com.has.backend.common.config.property.CookieProperties;
import com.has.backend.domain.auth.dto.JwtDto;
import com.has.backend.domain.auth.dto.request.LoginRequest;
import com.has.backend.domain.auth.dto.response.JwtResponse;
import com.has.backend.domain.auth.service.AuthService;
import com.has.backend.domain.auth.util.CookieUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "로그인 관련 API")
public class AuthController {

    private final AuthService authService;
    private final CookieProperties cookieProperties;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {

        JwtDto jwtDto = authService.login(request);

        response.setHeader("Authorization", "Bearer " + jwtDto.accessToken());
        response.addCookie(CookieUtil.createCookie("refresh", jwtDto.refreshToken(), cookieProperties.getMaxAge()));

        return ResponseEntity.ok(ApiResponse.ok(new JwtResponse(jwtDto.accessToken())));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @CookieValue(value = "refresh", required = false) String refreshToken,
            HttpServletResponse response) {

        String accessToken = (authorization != null && authorization.startsWith("Bearer "))
                ? authorization.split(" ")[1] : null;

        authService.logout(accessToken, refreshToken);
        response.addCookie(CookieUtil.createCookie("refresh", null, cookieProperties.getDeleteAge()));

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<JwtResponse>> reissue(@CookieValue("refresh") String refreshToken, HttpServletResponse response) {

        JwtDto jwtDto = authService.reissue(refreshToken);

        response.setHeader("Authorization", "Bearer " + jwtDto.accessToken());
        response.addCookie(CookieUtil.createCookie("refresh", jwtDto.refreshToken(), cookieProperties.getMaxAge()));

        return ResponseEntity.ok(ApiResponse.ok(new JwtResponse(jwtDto.accessToken())));
    }
}
