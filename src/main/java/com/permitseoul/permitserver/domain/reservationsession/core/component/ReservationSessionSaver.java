package com.permitseoul.permitserver.domain.reservationsession.core.component;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import com.permitseoul.permitserver.domain.reservationsession.core.repository.ReservationSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationSessionSaver {

    private final ReservationSessionRepository reservationSessionRepository;

    public ReservationSession saveReservationSession(final long userId,
                                                     final String orderId,
                                                     final String sessionKey) {
        final ReservationSessionEntity reservationSession = reservationSessionRepository.save(ReservationSessionEntity.create(userId, orderId, sessionKey));
        return ReservationSession.fromEntity(reservationSession);
    }
}
