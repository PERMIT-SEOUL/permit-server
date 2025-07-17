package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class ReservationBadRequestException extends ReservationApiException {
    public ReservationBadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
