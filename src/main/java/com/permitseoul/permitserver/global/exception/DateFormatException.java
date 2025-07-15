package com.permitseoul.permitserver.global.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class DateFormatException extends PermitGlobalException {
    private final ErrorCode errorCode;

    public DateFormatException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
