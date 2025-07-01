package com.permitseoul.permitserver.domain.user.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class UserUnAuthorizedException extends UserApiException {

    private final String errorMessage;

    public UserUnAuthorizedException(ErrorCode errorCode, String errorMessage) {
        super(errorCode);
        this.errorMessage = errorMessage;
    }

    public UserUnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
