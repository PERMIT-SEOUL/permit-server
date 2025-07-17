package com.permitseoul.permitserver.domain.reservationsession.core.domain;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReservationSession {
    private final Long reservationSessionsId;
    private final long userId;
    private final String orderId;
    private final String sessionKey;

    public static ReservationSession fromEntity(ReservationSessionEntity reservationSessionEntity) {
        return new ReservationSession(
                reservationSessionEntity.getReservationSessionsId(),
                reservationSessionEntity.getUserId(),
                reservationSessionEntity.getOrderId(),
                reservationSessionEntity.getSessionKey()
        );
    }
}
