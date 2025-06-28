package com.permitseoul.permitserver.auth.dto;

public record CookieDto(
        String accessToken,
        String RefreshToken
) {
    public static CookieDto of(final String accessToken, final String RefreshToken) {
        return new CookieDto(accessToken, RefreshToken);
    }
}
