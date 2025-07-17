package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class InSufficientReservationException extends ReservationApiException {
    public InSufficientReservationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
