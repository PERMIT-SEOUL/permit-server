package com.permitseoul.permitserver.domain.auth.core.jwt;

import com.permitseoul.permitserver.domain.auth.core.domain.RefreshToken;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthRTException;
import com.permitseoul.permitserver.domain.auth.core.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RefreshTokenManager {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshTokenInRedis(final long userId, final String refreshToken, final long ttlMillis) {
        try {
            final long ttlSeconds = ttlMillis / 1000; // ms → s
            refreshTokenRepository.save(RefreshToken.of(userId, refreshToken, ttlSeconds));
        } catch (DataAccessException e) {
            throw new AuthRTException();
        }
    }

    public void deleteRefreshToken(final long userId) {
            refreshTokenRepository.deleteById(userId);
    }

    public boolean matches(final long userId, final String refreshToken) {
            final RefreshToken originalRefreshToken = refreshTokenRepository.findById(userId).orElseThrow(AuthRTException::new);
            return Objects.equals(originalRefreshToken.getRefreshToken(), refreshToken);
    }

}
