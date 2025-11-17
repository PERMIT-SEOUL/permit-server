package com.permitseoul.permitserver.domain.admin.coupon.api.controller;

import com.permitseoul.permitserver.domain.admin.coupon.api.dto.request.CouponIssueRequest;
import com.permitseoul.permitserver.domain.admin.coupon.api.dto.request.CouponMemoUpdateRequest;
import com.permitseoul.permitserver.domain.admin.coupon.api.service.AdminCouponService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //쿠폰 조회 API
    @GetMapping("/{eventId}")
    public ResponseEntity<BaseResponse<?>> getCoupons(
            @PathVariable("eventId") long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, adminCouponService.getCouponsByEventId(eventId));
    }

    //쿠폰 메모 추가 API
    @PatchMapping("/memos")
    public ResponseEntity<BaseResponse<?>> updateCouponMemos(
            @RequestBody @Valid CouponMemoUpdateRequest couponMemoUpdateRequest
    ) {
        adminCouponService.updateCouponMemos(couponMemoUpdateRequest.coupons());
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
