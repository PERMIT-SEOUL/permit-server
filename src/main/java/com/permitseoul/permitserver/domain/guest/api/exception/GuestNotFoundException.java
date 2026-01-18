package com.permitseoul.permitserver.domain.guest.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class GuestNotFoundException extends GuestApiException {
    public GuestNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
