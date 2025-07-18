package com.permitseoul.permitserver.domain.auth.core.jwt;

import com.permitseoul.permitserver.global.domain.CookieType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

public abstract class CookieExtractor {


    public static Cookie extractCookie(final HttpServletRequest request, final CookieType cookieType) {
        final Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            throw cookieType.getException();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieType.getCookieName()))
                .findFirst()
                .orElseThrow(cookieType::getException);
    }
}
