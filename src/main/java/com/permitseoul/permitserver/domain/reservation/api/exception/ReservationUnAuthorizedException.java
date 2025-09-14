package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class ReservationUnAuthorizedException extends ReservationApiException {
  public ReservationUnAuthorizedException(ErrorCode errorCode) {
    super(errorCode);
  }
}
