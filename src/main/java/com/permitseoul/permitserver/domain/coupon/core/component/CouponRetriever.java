package com.permitseoul.permitserver.domain.coupon.core.component;

import com.permitseoul.permitserver.domain.coupon.core.domain.Coupon;
import com.permitseoul.permitserver.domain.coupon.core.domain.entity.CouponEntity;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponConflictException;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponNotfoundException;
import com.permitseoul.permitserver.domain.coupon.core.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponRetriever {
    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public void isExistCoupon(final String couponCode) {
        if(!couponRepository.existsByCouponCode(couponCode)) {
            throw new CouponNotfoundException();
        };
    }

    @Transactional(readOnly = true)
    public Coupon findCouponByCouponCode(final String couponCode) {
        final CouponEntity couponEntity = couponRepository.findByCouponCode(couponCode).orElseThrow(CouponNotfoundException::new);
        return Coupon.fromEntity(couponEntity);
    }

    @Transactional(readOnly = true)
    public void isCouponValid(final String couponCode) {
        if(!couponRepository.existsByCouponCodeAndUsedFalse(couponCode)) {
            throw new CouponConflictException();
        };
    }
}
