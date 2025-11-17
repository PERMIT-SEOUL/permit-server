package com.permitseoul.permitserver.domain.admin.coupon.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.coupon.api.dto.request.CouponMemoUpdateRequest;
import com.permitseoul.permitserver.domain.admin.coupon.api.dto.response.CouponResponse;
import com.permitseoul.permitserver.domain.coupon.core.component.CouponRetriever;
import com.permitseoul.permitserver.domain.coupon.core.component.CouponSaver;
import com.permitseoul.permitserver.domain.coupon.core.domain.Coupon;
import com.permitseoul.permitserver.domain.coupon.core.domain.entity.CouponEntity;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.InvalidRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCouponService {
    private final CouponSaver couponSaver;
    private final CouponRetriever couponRetriever;

    @Transactional
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

    @Transactional
    public void updateCouponMemos(final List<CouponMemoUpdateRequest.CouponMemoProp> couponMemos) {
        validCouponDuplicated(couponMemos);

        final Map<Long, String> couponMemoMap = couponMemos.stream()
                .collect(Collectors.toMap(
                        CouponMemoUpdateRequest.CouponMemoProp::couponId,
                        CouponMemoUpdateRequest.CouponMemoProp::memo
                        ));
        final List<Long> couponIds = new ArrayList<>(couponMemoMap.keySet());
        final List<CouponEntity> couponEntities = couponRetriever.findAllCouponEntitiesByIds(couponIds);
        validNotFoundCoupon(couponMemoMap, couponEntities);

        couponEntities.forEach(coupon -> {
            String memo = couponMemoMap.get(coupon.getCouponId());
            coupon.updateMemo(memo);
        });
    }

    private void validCouponDuplicated(final List<CouponMemoUpdateRequest.CouponMemoProp> couponMemos) {
        final long distinctCount = couponMemos.stream()
                .map(CouponMemoUpdateRequest.CouponMemoProp::couponId)
                .distinct()
                .count();
        if (distinctCount != couponMemos.size()) {
            throw new AdminApiException(ErrorCode.CONFLICT_DUPLICATE_COUPON_ID);
        }
    }

    private void validNotFoundCoupon(final Map<Long, String> couponMemoMap, final List<CouponEntity> couponEntities) {
        if (couponEntities.size() != couponMemoMap.size()) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_COUPON);
        }
    }
}
