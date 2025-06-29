package com.permitseoul.permitserver.global.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class PermitUnAuthorizedException extends PermitBaseException {
    private final ErrorCode errorCode;
    private final String message; // 선택적 메시지

    public PermitUnAuthorizedException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = null;
    }

    public PermitUnAuthorizedException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
