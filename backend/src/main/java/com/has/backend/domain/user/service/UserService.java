package com.has.backend.domain.user.service;

import com.has.backend.common.exception.CustomException;
import com.has.backend.domain.auth.dto.JwtDto;
import com.has.backend.domain.auth.jwt.JwtUtil;
import com.has.backend.domain.auth.repository.AccessTokenRepository;
import com.has.backend.domain.auth.repository.RefreshTokenRepository;
import com.has.backend.domain.user.dto.request.ChangePasswordRequest;
import com.has.backend.domain.user.entity.User;
import com.has.backend.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public JwtDto changePassword(Long userId, ChangePasswordRequest request) {

        String password = request.password();
        String newPassword = request.newPassword();

        User user = findUser(userId);

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new CustomException(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다.");

        user.updatePassword(passwordEncoder.encode(newPassword));

        // JWT iat는 초 단위로 저장되므로 밀리초 부분을 버려 새 토큰이 무효화되지 않도록 함
        long changedAtMs = (System.currentTimeMillis() / 1000) * 1000;
        refreshTokenRepository.deleteAll(user.getUsername());
        accessTokenRepository.saveInvalidationTime(user.getUsername(), changedAtMs);

        String username = user.getUsername();
        String newAccess = jwtUtil.createAccessToken(username, userId);
        String newRefresh = jwtUtil.createRefreshToken(username, userId);
        refreshTokenRepository.save(username, jwtUtil.getIdentifier(newRefresh), newRefresh);

        return new JwtDto(newAccess, newRefresh);

    }

    public User findUser(Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "올바르지 않은 계정입니다."));

        if (user.getDeletedAt() != null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "올바르지 않은 계정입니다.");
        }

        return user;
    }
}
