package com.permitseoul.permitserver.domain.event.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class NotFoundEventException extends EventApiException {
    public NotFoundEventException(ErrorCode errorCode) {
        super(errorCode);
    }
}
