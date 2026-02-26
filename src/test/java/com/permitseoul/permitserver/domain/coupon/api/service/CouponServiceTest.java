package com.permitseoul.permitserver.domain.coupon.api.service;

import com.permitseoul.permitserver.domain.coupon.api.dto.CouponValidateResponse;
import com.permitseoul.permitserver.domain.coupon.api.exception.NotFoundCouponException;
import com.permitseoul.permitserver.domain.coupon.core.component.CouponRetriever;
import com.permitseoul.permitserver.domain.coupon.core.domain.Coupon;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponNotfoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService 테스트")
class CouponServiceTest {

    @Mock
    private CouponRetriever couponRetriever;
    @InjectMocks
    private CouponService couponService;

    private static final String COUPON_CODE = "COUPON-2026";
    private static final long EVENT_ID = 100L;

    @Test
    @DisplayName("정상: 유효한 쿠폰 코드 검증 → 할인율 반환")
    void validateCouponSuccess() {
        final Coupon coupon = new Coupon(1L, EVENT_ID, COUPON_CODE, 10, "테스트 쿠폰", false, null, LocalDateTime.now());
        when(couponRetriever.findValidCouponByCodeAndEvent(COUPON_CODE, EVENT_ID)).thenReturn(coupon);

        final CouponValidateResponse result = couponService.validateCoupon(COUPON_CODE, EVENT_ID);

        assertThat(result.discountRate()).isEqualTo(10);
    }

    @Test
    @DisplayName("예외: 쿠폰 코드 미존재 → NotFoundCouponException")
    void throwsWhenCouponNotFound() {
        when(couponRetriever.findValidCouponByCodeAndEvent(COUPON_CODE, EVENT_ID))
                .thenThrow(new CouponNotfoundException());

        assertThatThrownBy(() -> couponService.validateCoupon(COUPON_CODE, EVENT_ID))
                .isInstanceOf(NotFoundCouponException.class);
    }
}
