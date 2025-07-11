package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotfoundException;
import com.permitseoul.permitserver.domain.reservation.core.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReservationRetriever {
    private final ReservationRepository reservationRepository;

    public Reservation getReservationByOrderIdAndAmount(final String orderId, final int amount, final long userId) {
        final ReservationEntity reservationEntity =  reservationRepository.findByOrderIdAndTotalAmountAndUserId(orderId, amount, userId).orElseThrow(ReservationNotfoundException::new);
        return Reservation.fromEntity(reservationEntity);
    }
}
