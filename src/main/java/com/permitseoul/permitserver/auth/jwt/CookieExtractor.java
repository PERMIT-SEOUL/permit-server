package com.permitseoul.permitserver.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.jwt.BadJwtException;

import java.util.Arrays;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieExtractor {

    public static Cookie getTokenCookie(final HttpServletRequest request) {
        return getCookie(request).orElseThrow(
                () -> new BadJwtException("Invalid JWT token") //todo: Exception 변경해야함
        );
    }

    private static Optional<Cookie> getCookie(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("accessToken"))
                .findFirst();
    }
}
