package com.permitseoul.permitserver.domain.payment.api;

import com.permitseoul.permitserver.domain.payment.api.exception.PaymentApiException;
import com.permitseoul.permitserver.domain.reservation.api.exception.ReservationApiException;
import com.permitseoul.permitserver.domain.reservation.api.exception.TossPaymentConfirmException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.payment")
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentApiException.class)
    public ResponseEntity<BaseResponse<?>> handlePaymentApiException(final PaymentApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }

    @ExceptionHandler(TossPaymentConfirmException.class)
    public ResponseEntity<BaseResponse<?>> handleTossPaymentConfirmException(final TossPaymentConfirmException e) {
        return ApiResponseUtil.failure(e.getErrorCode(), e.getTossMessage());
    }
}
