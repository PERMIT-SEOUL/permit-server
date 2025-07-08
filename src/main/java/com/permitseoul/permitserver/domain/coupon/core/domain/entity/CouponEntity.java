package com.permitseoul.permitserver.domain.coupon.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "coupons")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
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

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
}
