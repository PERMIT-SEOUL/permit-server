package com.permitseoul.permitserver.domain.coupon.core.component;

import com.permitseoul.permitserver.domain.coupon.core.domain.entity.CouponEntity;
import org.springframework.stereotype.Component;

@Component
public class CouponUpdater {
    public void updateCouponUsable(final CouponEntity couponEntity) {
        couponEntity.updateCouponUsable(false);
    }
}
