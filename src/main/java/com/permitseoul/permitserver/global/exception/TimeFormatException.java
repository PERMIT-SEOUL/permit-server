package com.permitseoul.permitserver.global.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class TimeFormatException extends PermitGlobalException {
    private final ErrorCode errorCode;

    public TimeFormatException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
