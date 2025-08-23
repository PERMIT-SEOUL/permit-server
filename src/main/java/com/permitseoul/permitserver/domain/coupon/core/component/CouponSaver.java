package com.permitseoul.permitserver.domain.coupon.core.component;

import com.permitseoul.permitserver.domain.coupon.core.domain.entity.CouponEntity;
import com.permitseoul.permitserver.domain.coupon.core.repository.CouponRepository;
import com.permitseoul.permitserver.global.TicketCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CouponSaver {
    private final CouponRepository couponRepository;

    @Transactional
    public void saveCoupons(final long eventId, final int discountRate, final int count) {
        final List<CouponEntity> coupons = IntStream.range(0, count)
                .mapToObj(i -> CouponEntity.create(
                        eventId,
                        TicketCodeGenerator.generateTicketCode(),
                        discountRate
                ))
                .toList();

        couponRepository.saveAll(coupons);
    }
}
