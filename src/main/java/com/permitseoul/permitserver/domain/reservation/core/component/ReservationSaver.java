package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservation.core.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ReservationSaver {
    private final ReservationRepository reservationRepository;

    public Reservation saveReservation(final long userId,
                                       final long eventId,
                                       final String orderId,
                                       final BigDecimal totalAmount,
                                       final String couponCode) {
        final ReservationEntity reservationEntity = reservationRepository.save(ReservationEntity.create(userId, eventId, orderId, totalAmount, couponCode));
        return Reservation.fromEntity(reservationRepository.save(reservationEntity));
    }
}
