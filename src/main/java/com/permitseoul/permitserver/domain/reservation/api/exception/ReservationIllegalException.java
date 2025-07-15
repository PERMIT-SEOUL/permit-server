package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class ReservationIllegalException extends ReservationApiException {
    public ReservationIllegalException(ErrorCode errorCode) {
        super(errorCode);
    }
}
