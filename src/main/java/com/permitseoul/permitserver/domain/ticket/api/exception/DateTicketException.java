package com.permitseoul.permitserver.domain.ticket.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class DateTicketException extends TicketApiException {
    public DateTicketException(ErrorCode errorCode) {
        super(errorCode);
    }
}
