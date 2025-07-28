package com.permitseoul.permitserver.domain.coupon.api.controller;

import com.permitseoul.permitserver.domain.coupon.api.dto.CouponValidateRequest;
import com.permitseoul.permitserver.domain.coupon.api.service.CouponService;
import com.permitseoul.permitserver.global.resolver.event.EventIdPathVariable;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;

    //쿠폰 검증 api
    @PostMapping("/validate/{eventId}")
    public ResponseEntity<BaseResponse<?>> validateCouponCode(
            @RequestBody @Valid final CouponValidateRequest couponValidateRequest,
            @EventIdPathVariable final Long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, couponService.validateCoupon(couponValidateRequest.couponCode(), eventId));
    }
}
