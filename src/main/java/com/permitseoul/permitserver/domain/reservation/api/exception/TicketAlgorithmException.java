package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class TicketAlgorithmException extends ReservationApiException {
  public TicketAlgorithmException(ErrorCode errorCode) {
    super(errorCode);
  }
}
