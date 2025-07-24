package com.permitseoul.permitserver.global.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class UrlDecodeException extends PermitGlobalException {

    private final ErrorCode errorCode;
    public UrlDecodeException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
