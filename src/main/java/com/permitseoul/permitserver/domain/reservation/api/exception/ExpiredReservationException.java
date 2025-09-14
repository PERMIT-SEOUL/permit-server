package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class ExpiredReservationException extends ReservationApiException {
    public ExpiredReservationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
