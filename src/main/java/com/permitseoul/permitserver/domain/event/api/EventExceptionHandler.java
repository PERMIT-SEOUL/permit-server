package com.permitseoul.permitserver.domain.event.api;

import com.permitseoul.permitserver.domain.event.api.exception.EventApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.event")
public class EventExceptionHandler {

    @ExceptionHandler(EventApiException.class)
    public ResponseEntity<BaseResponse<?>> handleEventApiException(final EventApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }
}
