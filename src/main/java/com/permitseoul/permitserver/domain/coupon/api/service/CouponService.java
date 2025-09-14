package com.permitseoul.permitserver.domain.coupon.api.service;

import com.permitseoul.permitserver.domain.coupon.api.dto.CouponValidateResponse;
import com.permitseoul.permitserver.domain.coupon.api.exception.NotFoundCouponException;
import com.permitseoul.permitserver.domain.coupon.core.component.CouponRetriever;
import com.permitseoul.permitserver.domain.coupon.core.domain.Coupon;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponNotfoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponRetriever couponRetriever;

    @Transactional(readOnly = true)
    public CouponValidateResponse validateCoupon(final String couponCode, final long eventId) {
        try {
            final Coupon coupon = couponRetriever.findValidCouponByCodeAndEvent(couponCode, eventId);
            return CouponValidateResponse.of(coupon.getDiscountRate());
        } catch (CouponNotfoundException e) {
            throw new NotFoundCouponException(ErrorCode.NOT_FOUND_COUPON_CODE);
        }
    }

}
