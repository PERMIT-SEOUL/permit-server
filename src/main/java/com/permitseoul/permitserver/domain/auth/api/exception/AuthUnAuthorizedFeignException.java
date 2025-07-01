package com.permitseoul.permitserver.domain.auth.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class AuthUnAuthorizedFeignException extends AuthApiException{
    private final String feignErrorMessage;

    public AuthUnAuthorizedFeignException(ErrorCode errorCode, String feignErrorMessage) {
        super(errorCode);
        this.feignErrorMessage = feignErrorMessage;
    }
}
