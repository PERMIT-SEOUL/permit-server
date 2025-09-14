package com.permitseoul.permitserver.global.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class ResolverException extends PermitGlobalException {
    private final ErrorCode errorCode;

    public ResolverException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
