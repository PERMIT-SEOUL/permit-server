package com.permitseoul.permitserver.domain.coupon.core.component;

import com.permitseoul.permitserver.domain.coupon.core.domain.entity.CouponEntity;
import org.springframework.stereotype.Component;

@Component
public class CouponUpdater {
    public void updateCouponUsed(final CouponEntity couponEntity, final boolean used) {
        couponEntity.updateCouponUsed(used);
    }
}
