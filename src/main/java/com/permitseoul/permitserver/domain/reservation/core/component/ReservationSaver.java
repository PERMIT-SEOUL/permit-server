package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservation.core.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationSaver {
    private final ReservationRepository reservationRepository;

    public Reservation saveReservation(final long userId,
                                       final long eventId,
                                       final String orderId,
                                       final int totalAmount,
                                       final String couponCode,
                                       final ReservationStatus status) {
        final ReservationEntity reservationEntity = reservationRepository.save(ReservationEntity.creat(userId, eventId, orderId, totalAmount, couponCode, status,null));
        return Reservation.fromEntity(reservationRepository.save(reservationEntity));
    }
}
