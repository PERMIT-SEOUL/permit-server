package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationUpdater {

    public void updateReservationStatus(final ReservationEntity reservationEntity, final ReservationStatus reservationStatus) {
        reservationEntity.updateReservationStatus(reservationStatus);
    }

    public void updateTossResponseTime(final ReservationEntity reservationEntity, final LocalDateTime now) {
        reservationEntity.updateTossPaymentResponseTime(now);
    }
}
