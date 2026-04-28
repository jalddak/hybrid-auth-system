package com.has.backend.domain.auth.service;

import com.has.backend.common.exception.CustomException;
import com.has.backend.domain.auth.dto.JwtDto;
import com.has.backend.domain.auth.dto.request.LoginRequest;
import com.has.backend.domain.auth.jwt.JwtUtil;
import com.has.backend.domain.auth.repository.AccessTokenRepository;
import com.has.backend.domain.auth.repository.RefreshTokenRepository;
import com.has.backend.domain.auth.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;

    public JwtDto login(LoginRequest request) {

        String username = request.username();
        String password = request.password();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManager.authenticate(authToken);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        username = customUserDetails.getUsername();
        Long userId = customUserDetails.getUserId();

        String access = jwtUtil.createAccessToken(username, userId);
        String refresh = jwtUtil.createRefreshToken(username, userId);

        refreshTokenRepository.save(username, jwtUtil.getIdentifier(refresh), refresh);

        return new JwtDto(access, refresh);
    }

    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null) {
            try {
                Claims claims = jwtUtil.extractAllClaims(accessToken);
                long remainingMs = claims.getExpiration().getTime() - System.currentTimeMillis();
                if (remainingMs > 0) {
                    accessTokenRepository.blacklist(accessToken, remainingMs);
                }
            } catch (Exception e) {
                log.warn("Failed to blacklist access token during logout: {}", e.getMessage());
            }
        }

        if (refreshToken != null) {
            try {
                Claims claims = jwtUtil.extractAllClaims(refreshToken);
                String username = claims.get("username", String.class);
                String identifier = claims.get("identifier", String.class);
                refreshTokenRepository.delete(username, identifier);
            } catch (Exception e) {
                log.warn("Failed to delete refresh token during logout: {}", e.getMessage());
            }
        }
    }

    public JwtDto reissue(String refreshToken) {

        Claims claims = null;
        try {
            claims = jwtUtil.extractAllClaims(refreshToken);
        } catch (JwtException e) {
            log.error("refresh token error");
            throw new CustomException(HttpStatus.UNAUTHORIZED, "잘못된 리프레시 토큰입니다.");
        }

        String username = claims.get("username", String.class);
        String category = claims.get("category", String.class);
        Long userId = claims.get("userId", Long.class);
        String identifier = claims.get("identifier", String.class);

        if (!"refresh".equals(category)) {
            log.error("Invalid refresh token");
            throw new CustomException(HttpStatus.UNAUTHORIZED, "잘못된 리프레시 토큰입니다.");
        }

        if (!refreshTokenRepository.exists(username, identifier, refreshToken)) {
            log.error("Refresh token not found in Redis. username={}", username);
            throw new CustomException(HttpStatus.UNAUTHORIZED, "잘못된 리프레시 토큰입니다.");
        }

        refreshTokenRepository.delete(username, identifier);

        String newAccess = jwtUtil.createAccessToken(username, userId);
        String newRefresh = jwtUtil.createRefreshToken(username, userId);
        refreshTokenRepository.save(username, jwtUtil.getIdentifier(newRefresh), newRefresh);

        return new JwtDto(newAccess, newRefresh);
    }
}
