package com.permitseoul.permitserver.domain.ticket.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class ConflictTicketException extends TicketApiException {
  public ConflictTicketException(ErrorCode errorCode) {
    super(errorCode);
  }
}
