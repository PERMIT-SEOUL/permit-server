package com.permitseoul.permitserver.global.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class FilterException extends PermitGlobalException {

    private final ErrorCode errorCode;

    public FilterException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
