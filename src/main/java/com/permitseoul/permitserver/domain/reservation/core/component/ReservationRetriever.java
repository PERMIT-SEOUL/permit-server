package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservation.core.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ReservationRetriever {
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public Reservation findReservationByOrderIdAndAmountAndUserId(final String orderId, final BigDecimal amount, final long userId) {
        final ReservationEntity reservationEntity = reservationRepository.findByOrderIdAndTotalAmountAndUserId(orderId, amount, userId).orElseThrow(
                ReservationNotFoundException::new
        );
        return Reservation.fromEntity(reservationEntity);
    }

    @Transactional(readOnly = true)
    public Reservation findReservationById(final long reservationId) {
        final ReservationEntity reservationEntity = reservationRepository.findById(reservationId).orElseThrow(
                ReservationNotFoundException::new
        );
        return Reservation.fromEntity(reservationEntity);
    }

    @Transactional(readOnly = true)
    public ReservationEntity findReservationEntityById(final long reservationId) {
         return reservationRepository.findById(reservationId).orElseThrow(
                 ReservationNotFoundException::new
         );
    }

    @Transactional(readOnly = true)
    public Reservation findReservationByIdAndUserId(final long reservationId, final long userId) {
        final ReservationEntity reservationEntity = reservationRepository.findByReservationIdAndUserId(reservationId, userId).orElseThrow(
                ReservationNotFoundException::new
        );
        return Reservation.fromEntity(reservationEntity);
    }

    @Transactional(readOnly = true)
    public Reservation findReservationByOrderIdAndUserId(final String orderId, final long userId) {
        final ReservationEntity reservationEntity = reservationRepository.findByOrderIdAndUserId(orderId, userId).orElseThrow(
                ReservationNotFoundException::new
        );
        return Reservation.fromEntity(reservationEntity);
    }
}
