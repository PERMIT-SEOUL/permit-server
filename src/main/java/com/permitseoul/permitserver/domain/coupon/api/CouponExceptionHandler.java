package com.permitseoul.permitserver.domain.coupon.api;

import com.permitseoul.permitserver.domain.coupon.api.exception.CouponApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.coupon")
public class CouponExceptionHandler {

    @ExceptionHandler(CouponApiException.class)
    public ResponseEntity<BaseResponse<?>> handleCouponApiException(final CouponApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }
}
