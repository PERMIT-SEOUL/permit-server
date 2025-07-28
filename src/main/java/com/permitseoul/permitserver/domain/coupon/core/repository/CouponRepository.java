package com.permitseoul.permitserver.domain.coupon.core.repository;

import com.permitseoul.permitserver.domain.coupon.core.domain.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    boolean existsByCouponCode(final String couponCode);
    boolean existsByCouponCodeAndUsedFalse(final String couponCode);
    Optional<CouponEntity> findByCouponCode(final String couponCode);
    Optional<CouponEntity> findByCouponCodeAndEventIdAndUsedFalse(final String couponCode, final long eventId);

}
