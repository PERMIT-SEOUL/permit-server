package com.permitseoul.permitserver.domain.user.api;

import com.permitseoul.permitserver.domain.user.api.exception.UserApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.user")
public class UserExceptionHandler {

    @ExceptionHandler(UserApiException.class)
    public ResponseEntity<BaseResponse<?>> handleUserApiException(final UserApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }


}
