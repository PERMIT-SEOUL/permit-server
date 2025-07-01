package com.permitseoul.permitserver.domain.auth.core.jwt;

import com.permitseoul.permitserver.domain.auth.core.exception.AuthCookieException;
import com.permitseoul.permitserver.global.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieExtractor {

    public static Cookie getTokenCookie(final HttpServletRequest request) {
        return getCookie(request).orElseThrow(AuthCookieException::new);
    }

    private static Optional<Cookie> getCookie(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(Constants.ACCESS_TOKEN))
                .findFirst();
    }
}
