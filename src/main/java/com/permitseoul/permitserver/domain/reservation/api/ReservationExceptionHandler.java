package com.permitseoul.permitserver.domain.reservation.api;

import com.permitseoul.permitserver.domain.reservation.api.exception.ReservationApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.reservation")
public class ReservationExceptionHandler {

    @ExceptionHandler(ReservationApiException.class)
    public ResponseEntity<BaseResponse<?>> handleReservationApiException(final ReservationApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }
}
