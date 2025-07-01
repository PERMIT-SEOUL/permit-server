package com.permitseoul.permitserver.domain.user.api;

import com.permitseoul.permitserver.domain.user.api.exception.UserApiException;
import com.permitseoul.permitserver.domain.user.api.exception.UserNotFoundApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.user")
public class UserExceptionHandler {

    @ExceptionHandler(UserApiException.class)
    public ResponseEntity<BaseResponse<?>> handleUserApiException(final UserApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }

    @ExceptionHandler(UserNotFoundApiException.class)
    public ResponseEntity<BaseResponse<?>> handleUserNotFoundApiException(final UserNotFoundApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode(), e.getSocialAccessToken());
    }
}
