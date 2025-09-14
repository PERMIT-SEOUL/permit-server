package com.permitseoul.permitserver.domain.coupon.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class NotFoundCouponException extends CouponApiException{
    public NotFoundCouponException(ErrorCode errorCode) {
        super(errorCode);
    }
}
