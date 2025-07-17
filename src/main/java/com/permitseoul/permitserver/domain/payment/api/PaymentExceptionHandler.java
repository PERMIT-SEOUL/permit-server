package com.permitseoul.permitserver.domain.payment.api;

import com.permitseoul.permitserver.domain.payment.api.exception.PaymentApiException;
import com.permitseoul.permitserver.domain.reservation.api.exception.ReservationApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.payment")
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentApiException.class)
    public ResponseEntity<BaseResponse<?>> handlePaymentApiException(final PaymentApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }
}
