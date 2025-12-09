package com.permitseoul.permitserver.domain.auth.core.jwt;

import com.permitseoul.permitserver.global.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieCreatorUtil {
    private static JwtProperties jwtProperties;
    private static final long ACCESS_COOKIE_EXTRA_SECONDS = 5L * 60;   // 5분
    private static final long REFRESH_COOKIE_EXTRA_SECONDS = 15L * 60; // 15분
    private static final long RESERVED_MAX_AGE = 10L * 60; // 10분(10분간 선점 가능)

    public static ResponseCookie createReservationSessionCookie(final String sessionKey) {
        return ResponseCookie.from(Constants.RESERVATION_SESSION_KEY, sessionKey)
                .maxAge(RESERVED_MAX_AGE)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public static ResponseCookie createAccessTokenCookie(final String accessToken, final long accessTokenExpirationMillis) {
        final long maxAgeSeconds = toCookieMaxAgeSeconds(accessTokenExpirationMillis, ACCESS_COOKIE_EXTRA_SECONDS);
        return ResponseCookie.from(Constants.ACCESS_TOKEN, accessToken)
                .maxAge(maxAgeSeconds)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public static ResponseCookie createRefreshTokenCookie(final String refreshToken,  final long refreshTokenExpirationMillis) {
        final long maxAgeSeconds = toCookieMaxAgeSeconds(refreshTokenExpirationMillis, REFRESH_COOKIE_EXTRA_SECONDS);
        return ResponseCookie.from(Constants.REFRESH_TOKEN, refreshToken)
                .maxAge(maxAgeSeconds)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public static ResponseCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(Constants.ACCESS_TOKEN, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public static ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(Constants.REFRESH_TOKEN, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    private static long toCookieMaxAgeSeconds(long jwtExpirationMillis, long extraSeconds) {
        long baseSeconds = jwtExpirationMillis / 1000;
        return baseSeconds + extraSeconds;
    }
}
