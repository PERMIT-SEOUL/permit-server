package com.permitseoul.permitserver.domain.admin.coupon.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CouponIssueRequest(
        @Positive(message = "eventId가 양수가 아닙니다.")
        @NotNull(message = "eventId가 null입니다.")
        long eventId,

        @Positive(message = "discountRate가 양수가 아닙니다.")
        @NotNull(message = "discountRate가 null입니다.")
        int discountRate,

        @Positive(message = "count가 양수가 아닙니다.")
        @NotNull(message = "count가 null입니다.")
        int count

) {
}
