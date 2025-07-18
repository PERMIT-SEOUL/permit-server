package com.permitseoul.permitserver.global.domain;

import com.permitseoul.permitserver.domain.auth.core.exception.AuthCookieException;
import com.permitseoul.permitserver.domain.reservation.api.exception.ReservationSessionCookieException;
import com.permitseoul.permitserver.global.Constants;
import lombok.Getter;

@Getter
public enum CookieType {
    ACCESS_TOKEN(Constants.ACCESS_TOKEN, new AuthCookieException()),
    RESERVATION_SESSION(Constants.RESERVATION_SESSION_KEY, new ReservationSessionCookieException());

    private final String cookieName;
    private final RuntimeException exception;

    CookieType(String cookieName, RuntimeException exception) {
        this.cookieName = cookieName;
        this.exception = exception;
    }

}

