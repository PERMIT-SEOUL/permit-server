package com.permitseoul.permitserver.domain.admin.coupon.api.controller;

import com.permitseoul.permitserver.domain.admin.coupon.api.dto.CouponIssueRequest;
import com.permitseoul.permitserver.domain.admin.coupon.api.service.AdminCouponService;
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

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {
    private final AdminCouponService adminCouponService;

    //쿠폰 생성 API
    @PostMapping()
    public ResponseEntity<BaseResponse<?>> issueCoupons(
            @RequestBody @Valid CouponIssueRequest couponIssueRequest
    ) {
        adminCouponService.issueCoupons(couponIssueRequest.eventId(), couponIssueRequest.discountRate(), couponIssueRequest.count());
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
