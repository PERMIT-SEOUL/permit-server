package com.permitseoul.permitserver.domain.admin.coupon.api.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CouponResponse(
        long couponId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime createDate,
        int discountRate,
        String couponCode,
        String memo,
        boolean usable
) {
    public static CouponResponse of(final long couponId,
                                  final LocalDateTime createDate,
                                  final int discountRate,
                                  final String couponCode,
                                  final String memo,
                                  final boolean usable
    ) {
        return new CouponResponse(couponId, createDate, discountRate, couponCode, memo, usable);
    }
}
