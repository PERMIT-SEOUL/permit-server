package com.permitseoul.permitserver.domain.auth.core.jwt;

import com.permitseoul.permitserver.global.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieCreatorUtil {
    private static final long AT_MAX_AGE = 365L * 24 * 60 * 60 * 1000; // todo: 추후 변경
    private static final long RT_MAX_AGE = 369L * 24 * 60 * 60 * 1000; // todo: 추후 변경
    private static final long RESERVED_MAX_AGE = 10L * 60 * 1000; // 10분(10분간 선점 가능)
    private static final String SESSION_NAME = "sessionKey";

    public static ResponseCookie createReservationSessionCookie(final String sessionKey) {
        return ResponseCookie.from(SESSION_NAME, sessionKey)
                .maxAge(RESERVED_MAX_AGE)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public static ResponseCookie createAccessTokenCookie(final String accessToken) {
        return ResponseCookie.from(Constants.ACCESS_TOKEN, accessToken)
                .maxAge(AT_MAX_AGE)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public static ResponseCookie createRefreshTokenCookie(final String refreshToken) {
        return ResponseCookie.from(Constants.REFRESH_TOKEN, refreshToken)
                .maxAge(RT_MAX_AGE)
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
