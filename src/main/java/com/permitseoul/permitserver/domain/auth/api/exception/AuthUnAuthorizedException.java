package com.permitseoul.permitserver.domain.auth.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class AuthUnAuthorizedException extends AuthApiException {
    public AuthUnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
