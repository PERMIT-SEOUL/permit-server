package com.permitseoul.permitserver.domain.admin.coupon.api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CouponMemoUpdateRequest(
        @NotEmpty(message = "coupon List가 null입니다.")
        List<CouponMemoProp> coupons
) {
    public record CouponMemoProp(
            @NotNull(message = "couponId가 null입니다.")
            long couponId,
            @NotNull(message = "couponMemo가 null입니다.")
            String memo
    ) { }
}
