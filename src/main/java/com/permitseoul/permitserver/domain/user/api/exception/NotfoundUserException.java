package com.permitseoul.permitserver.domain.user.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class NotfoundUserException extends UserApiException {
    public NotfoundUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
