package com.permitseoul.permitserver.domain.auth.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class AuthSocialNotFoundApiException extends AuthApiException {
    private final String socialAccessToken;

    public AuthSocialNotFoundApiException(ErrorCode errorCode, String socialAccessToken) {
        super(errorCode);
        this.socialAccessToken = socialAccessToken;
    }
}
