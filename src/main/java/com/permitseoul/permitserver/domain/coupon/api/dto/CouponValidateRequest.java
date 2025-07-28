package com.permitseoul.permitserver.domain.coupon.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CouponValidateRequest(
        @NotBlank(message = "쿠폰코드는 비어있으면 안됩니다.")
        String couponCode
) {
}
