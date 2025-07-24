package com.permitseoul.permitserver.global.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class UrlSecureException extends PermitGlobalException {

    private final ErrorCode errorCode;
    public UrlSecureException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
