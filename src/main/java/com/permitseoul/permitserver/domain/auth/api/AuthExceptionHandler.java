package com.permitseoul.permitserver.domain.auth.api;

import com.permitseoul.permitserver.domain.auth.api.exception.AuthApiException;
import com.permitseoul.permitserver.domain.user.api.exception.UserUnAuthorizedException;
import com.permitseoul.permitserver.domain.user.api.exception.UserNotFoundApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.auth")
public class AuthExceptionHandler {

    @ExceptionHandler(UserUnAuthorizedException.class)
    public ResponseEntity<BaseResponse<?>> handlePermitUnAuthorizedException(final UserUnAuthorizedException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }


    @ExceptionHandler(AuthApiException.class)
    public ResponseEntity<BaseResponse<?>> handleCakeApiBaseException(final AuthApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }

    @ExceptionHandler(UserNotFoundApiException.class)
    public ResponseEntity<BaseResponse<?>> handlePermitUserNotFoundException(final UserNotFoundApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode(), e.getSocialAccessToken());
    }
}
