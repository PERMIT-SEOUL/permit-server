package com.permitseoul.permitserver.domain.coupon.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @Column(name = "discount_rates")
    private int discountRates;

    private CouponEntity(long eventId, String couponCode, int discountRates) {
        this.eventId = eventId;
        this.couponCode = couponCode;
        this.discountRates = discountRates;
    }
}
