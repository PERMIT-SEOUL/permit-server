package com.permitseoul.permitserver.domain.user.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class UserSocialNotFoundApiException extends UserApiException {
    private final String socialAccessToken;

    public UserSocialNotFoundApiException(ErrorCode errorCode, String socialAccessToken) {
        super(errorCode);
        this.socialAccessToken = socialAccessToken;
    }
}
