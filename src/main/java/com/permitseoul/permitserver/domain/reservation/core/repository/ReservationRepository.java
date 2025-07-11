package com.permitseoul.permitserver.domain.reservation.core.repository;

import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    Optional<ReservationEntity> findByOrderIdAndTotalAmountAndUserId(final String orderId, int totalAmount, long userId);
}
