package com.permitseoul.permitserver.domain.auth.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class AuthRedisException extends AuthApiException {
    public AuthRedisException(ErrorCode errorCode) {
        super(errorCode);
    }
}
