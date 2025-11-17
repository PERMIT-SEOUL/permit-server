package com.permitseoul.permitserver.domain.admin.coupon.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CouponMemoUpdateRequest(
        @NotEmpty(message = "coupon List가 null입니다.")
        @Valid
        List<CouponMemoProp> coupons
) {
    public record CouponMemoProp(
            @Min(value = 1, message = "couponId는 1 이상이어야 합니다.")
            long couponId,
            @NotNull(message = "couponMemo가 null입니다.")
            String memo
    ) { }
}
