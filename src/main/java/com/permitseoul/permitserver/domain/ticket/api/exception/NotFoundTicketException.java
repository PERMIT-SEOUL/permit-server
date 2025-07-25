package com.permitseoul.permitserver.domain.ticket.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class NotFoundTicketException extends TicketApiException {
  public NotFoundTicketException(ErrorCode errorCode) {
    super(errorCode);
  }
}
