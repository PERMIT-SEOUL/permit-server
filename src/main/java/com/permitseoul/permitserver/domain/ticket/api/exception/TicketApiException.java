package com.permitseoul.permitserver.domain.ticket.api.exception;

import com.permitseoul.permitserver.domain.ticket.TicketBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class TicketApiException extends TicketBaseException {
    private final ErrorCode errorCode;
}
