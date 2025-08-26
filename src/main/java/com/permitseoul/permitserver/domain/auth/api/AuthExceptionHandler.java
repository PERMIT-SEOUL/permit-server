package com.permitseoul.permitserver.domain.auth.api;

import com.permitseoul.permitserver.domain.auth.api.exception.AuthApiException;
import com.permitseoul.permitserver.domain.auth.api.exception.AuthUnAuthorizedFeignException;
import com.permitseoul.permitserver.domain.auth.api.exception.AuthSocialNotFoundApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.auth")
public class AuthExceptionHandler {

    @ExceptionHandler(AuthUnAuthorizedFeignException.class)
    public ResponseEntity<BaseResponse<?>> handleAuthUnAuthorizedFeignException(final AuthUnAuthorizedFeignException e) {
        return ApiResponseUtil.failure(e.getErrorCode(), e.getFeignErrorMessage());
    }

    @ExceptionHandler(AuthSocialNotFoundApiException.class)
    public ResponseEntity<BaseResponse<?>> handleAuthSocialNotFoundApiException(final AuthSocialNotFoundApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode(), e.getSocialAccessToken());
    }

    @ExceptionHandler(AuthApiException.class)
    public ResponseEntity<BaseResponse<?>> handleAuthApiException(final AuthApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }

}
