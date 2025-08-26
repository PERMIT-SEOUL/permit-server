package com.permitseoul.permitserver.domain.ticket.api;

import com.permitseoul.permitserver.domain.ticket.api.exception.TicketApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.ticket")
public class TicketExceptionHandler {
    @ExceptionHandler(TicketApiException.class)
    public ResponseEntity<BaseResponse<?>> handleTicketApiException(TicketApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }
}
