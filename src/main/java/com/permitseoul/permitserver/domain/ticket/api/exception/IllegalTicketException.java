package com.permitseoul.permitserver.domain.ticket.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class IllegalTicketException extends TicketApiException {
    public IllegalTicketException(ErrorCode errorCode) {
        super(errorCode);
    }
}
