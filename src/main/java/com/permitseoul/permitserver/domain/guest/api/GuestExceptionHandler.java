package com.permitseoul.permitserver.domain.guest.api;

import com.permitseoul.permitserver.domain.guest.api.exception.GuestApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.guest")
public class GuestExceptionHandler {

    @ExceptionHandler(GuestApiException.class)
    public ResponseEntity<BaseResponse<?>> handleGuestApiException(final GuestApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }
}
