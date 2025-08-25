package com.permitseoul.permitserver.domain.coupon.core.domain;

import com.permitseoul.permitserver.domain.coupon.core.domain.entity.CouponEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Coupon {
    private final Long couponId;
    private final long eventId;
    private final String couponCode;
    private final int discountRate;
    private final String memo;
    private final boolean used;
    private final LocalDateTime createAt;

    public static Coupon fromEntity(final CouponEntity couponEntity) {
        return new Coupon(
                couponEntity.getCouponId(),
                couponEntity.getEventId(),
                couponEntity.getCouponCode(),
                couponEntity.getDiscountRate(),
                couponEntity.getMemo(),
                couponEntity.isUsed(),
                couponEntity.getCreatedAt()
        );
    }
}
