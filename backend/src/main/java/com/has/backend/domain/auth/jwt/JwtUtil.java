package com.has.backend.domain.auth.jwt;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.has.backend.common.config.property.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessTokenValidity;
    private final Long refreshTokenValidity;

    public JwtUtil(JwtProperties jwtProperties) {
        this.secretKey = new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTokenValidity = jwtProperties.getAccessTokenValidity();
        this.refreshTokenValidity = jwtProperties.getRefreshTokenValidity();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String createAccessToken(String username, Long userId) {

        return Jwts.builder()
                .claim("category", "access")
                .claim("username", username)
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String username, Long userId) {
        String identifier = NanoIdUtils.randomNanoId();

        return Jwts.builder()
                .claim("category", "refresh")
                .claim("identifier", identifier)
                .claim("username", username)
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(secretKey)
                .compact();
    }

    public String getIdentifier(String token) {
        return extractAllClaims(token).get("identifier", String.class);
    }
}
