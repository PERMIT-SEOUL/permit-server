package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationUpdater {

    public void updateReservationStatus(final ReservationEntity reservationEntity, ReservationStatus reservationStatus) {
        reservationEntity.updateReservationStatus(reservationStatus);
    }
}
