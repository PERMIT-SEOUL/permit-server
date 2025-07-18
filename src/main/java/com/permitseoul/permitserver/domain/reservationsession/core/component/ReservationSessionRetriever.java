package com.permitseoul.permitserver.domain.reservationsession.core.component;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import com.permitseoul.permitserver.domain.reservationsession.core.exception.ReservationSessionNotFoundException;
import com.permitseoul.permitserver.domain.reservationsession.core.repository.ReservationSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationSessionRetriever {
    private final ReservationSessionRepository reservationSessionRepository;

    public ReservationSession getValidatedReservationSession(final long userId, final String sessionKey, final LocalDateTime now) {
        final ReservationSessionEntity reservationSession = reservationSessionRepository.findValidSession(userId, sessionKey, now).orElseThrow(ReservationSessionNotFoundException::new);
        return ReservationSession.fromEntity(reservationSession);
    }
}
