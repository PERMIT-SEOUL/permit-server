package com.permitseoul.permitserver.domain.coupon.api.exception;

import com.permitseoul.permitserver.domain.coupon.CouponBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CouponApiException extends CouponBaseException {
    private final ErrorCode errorCode;
}
