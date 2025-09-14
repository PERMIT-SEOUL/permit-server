package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class ConflictReservationException extends ReservationApiException {
    public ConflictReservationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
