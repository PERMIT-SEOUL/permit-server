package com.permitseoul.permitserver.domain.coupon.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @Column(name = "discount_rates", nullable = false)
    private int discountRate;

    @Column(name = "memo")
    private String memo;

    @Getter
    @Column(name = "usable")
    private boolean usable;

    private CouponEntity(long eventId, String couponCode, int discountRate) {
        this.eventId = eventId;
        this.couponCode = couponCode;
        this.discountRate = discountRate;
        this.usable = true;
    }

    public static CouponEntity create(final long eventId,
                                      final String couponCode,
                                      final int discountRate) {
        return new CouponEntity(eventId, couponCode, discountRate);
    }
}
