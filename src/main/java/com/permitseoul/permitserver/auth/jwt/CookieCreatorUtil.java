package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.global.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieCreatorUtil {
    private static final long DEFAULT_MAX_AGE = 365L * 24 * 60 * 60; // todo: 추후 변경

    public static ResponseCookie createAccessTokenCookie(final String accessToken) {
        return ResponseCookie.from(Constants.ACCESS_TOKEN, accessToken)
                .maxAge(DEFAULT_MAX_AGE)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    public static ResponseCookie createRefreshTokenCookie(final String refreshToken) {
        return ResponseCookie.from(Constants.REFRESH_TOKEN, refreshToken)
                .maxAge(DEFAULT_MAX_AGE)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }
}
