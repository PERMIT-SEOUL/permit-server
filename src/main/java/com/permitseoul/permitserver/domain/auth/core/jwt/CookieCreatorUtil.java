package com.permitseoul.permitserver.domain.auth.core.jwt;

import com.permitseoul.permitserver.global.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieCreatorUtil {
    private static final long COOKIE_MAX_AGE = 2L * 60; //7L * 24 * 60 * 60; //일주일
    private static final long RESERVED_MAX_AGE = 7L * 60; // 7분(7분간 선점 가능)

    public static ResponseCookie createReservationSessionCookie(final String sessionKey) {
        return ResponseCookie.from(Constants.RESERVATION_SESSION_KEY, sessionKey)
                .maxAge(RESERVED_MAX_AGE)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public static ResponseCookie createAccessTokenCookie(final String accessToken) {
        return ResponseCookie.from(Constants.ACCESS_TOKEN, accessToken)
                .maxAge(COOKIE_MAX_AGE)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public static ResponseCookie createRefreshTokenCookie(final String refreshToken) {
        return ResponseCookie.from(Constants.REFRESH_TOKEN, refreshToken)
                .maxAge(COOKIE_MAX_AGE)
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
}
