package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.domain.reservation.ReservationBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ReservationApiException extends ReservationBaseException {
    private final ErrorCode errorCode;
}
