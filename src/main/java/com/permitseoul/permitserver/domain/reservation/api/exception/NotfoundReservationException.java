package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class NotfoundReservationException extends ReservationApiException {
    public NotfoundReservationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
