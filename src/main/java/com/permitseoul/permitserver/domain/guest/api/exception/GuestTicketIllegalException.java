package com.permitseoul.permitserver.domain.guest.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class GuestTicketIllegalException extends GuestApiException{
    public GuestTicketIllegalException(ErrorCode errorCode) {
        super(errorCode);
    }
}
