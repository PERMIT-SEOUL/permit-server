package com.permitseoul.permitserver.domain.coupon.api.dto;

import jakarta.validation.constraints.Positive;

public record CouponValidateResponse(
        @Positive(message = "할인비율은 0보다 커야합니다.")
        int discountRate
) {
        public static CouponValidateResponse of(final int discountRate) {
                return new CouponValidateResponse(discountRate);
        }
}
