package com.permitseoul.permitserver.domain.admin.coupon.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CouponIssueRequest(
        @Positive(message = "eventId가 양수가 아닙니다.")
        @Min(value = 1, message = "eventId가 1보다 작습니다.")
        long eventId,

        @Positive(message = "discountRate가 양수가 아닙니다.")
        @Min(value = 1, message = "discountRate가 1보다 작습니다.")
        @Max(value = 100, message = "discountRate가 100보다 큽니다.")
        int discountRate,

        @Positive(message = "count가 양수가 아닙니다.")
        @Min(value = 1, message = "count가 1보다 작습니다.")
        int count

) {
}
