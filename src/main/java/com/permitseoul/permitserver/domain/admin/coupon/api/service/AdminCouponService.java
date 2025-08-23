package com.permitseoul.permitserver.domain.admin.coupon.api.service;

import com.permitseoul.permitserver.domain.coupon.core.component.CouponSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCouponService {
    private final CouponSaver couponSaver;

    public void issueCoupons(final long eventId, final int discountRate, final int count) {
        couponSaver.saveCoupons(eventId, discountRate, count);
    }
}
