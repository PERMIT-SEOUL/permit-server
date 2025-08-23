package com.permitseoul.permitserver.domain.admin.coupon.api.service;

import com.permitseoul.permitserver.domain.admin.coupon.api.dto.response.CouponResponse;
import com.permitseoul.permitserver.domain.coupon.core.component.CouponRetriever;
import com.permitseoul.permitserver.domain.coupon.core.component.CouponSaver;
import com.permitseoul.permitserver.domain.coupon.core.domain.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCouponService {
    private final CouponSaver couponSaver;
    private final CouponRetriever couponRetriever;

    public void issueCoupons(final long eventId, final int discountRate, final int count) {
        couponSaver.saveCoupons(eventId, discountRate, count);
    }

    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponsByEventId(final long eventId) {
        final List<Coupon> coupons = couponRetriever.getCouponsByEventId(eventId);
        return coupons.stream()
                .map(coupon -> CouponResponse.of(
                                coupon.getCouponId(),
                                coupon.getCreateAt(),
                                coupon.getDiscountRate(),
                                coupon.getCouponCode(),
                                coupon.getMemo(),
                                coupon.isUsed()
                        )
                )
                .toList();
    }
}
