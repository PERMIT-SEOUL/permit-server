package com.permitseoul.permitserver.domain.user.api.exception;

import com.permitseoul.permitserver.domain.user.UserBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public abstract class UserApiException extends UserBaseException {
    private final ErrorCode errorCode;

    protected UserApiException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
