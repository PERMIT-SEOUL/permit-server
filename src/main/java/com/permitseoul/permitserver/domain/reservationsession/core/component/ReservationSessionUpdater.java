package com.permitseoul.permitserver.domain.reservationsession.core.component;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSessionStatus;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationSessionUpdater {

    public void updateReservationSessionStatus(final ReservationSessionEntity reservationSessionEntity,
                                               final ReservationSessionStatus reservationSessionStatus) {
        reservationSessionEntity.updateToReservationSessionEntityToSuccessful(reservationSessionStatus);
    }
}
