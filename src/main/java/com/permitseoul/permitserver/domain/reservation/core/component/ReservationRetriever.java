package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotfoundException;
import com.permitseoul.permitserver.domain.reservation.core.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class ReservationRetriever {
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public Reservation findReservationByOrderIdAndAmount(final String orderId, final int amount, final long userId) {
        final ReservationEntity reservationEntity =  reservationRepository.findByOrderIdAndTotalAmountAndUserId(orderId, amount, userId).orElseThrow(ReservationNotfoundException::new);
        return Reservation.fromEntity(reservationEntity);
    }
}
