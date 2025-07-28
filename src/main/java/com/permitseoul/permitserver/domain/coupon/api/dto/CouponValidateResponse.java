package com.permitseoul.permitserver.domain.coupon.api.dto;

import jakarta.validation.constraints.Positive;

public record CouponValidateResponse(
        @Positive(message = "할인비율은 0보다 커야합니다.")
        int salesRate
) {
        public static CouponValidateResponse of(final int salesRate) {
                return new CouponValidateResponse(salesRate);
        }
}
