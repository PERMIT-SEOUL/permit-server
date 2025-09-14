package com.permitseoul.permitserver.domain.reservation.core.repository;

import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    Optional<ReservationEntity> findByOrderIdAndTotalAmountAndUserId(final String orderId, BigDecimal totalAmount, long userId);
    Optional<ReservationEntity> findByOrderIdAndUserId(final String orderId, final long userId);
    Optional<ReservationEntity> findByReservationIdAndUserId(final long id, long userId);
}
