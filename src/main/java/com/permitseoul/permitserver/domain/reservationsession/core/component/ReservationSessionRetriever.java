package com.permitseoul.permitserver.domain.reservationsession.core.component;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import com.permitseoul.permitserver.domain.reservationsession.core.exception.ReservationSessionNotFoundException;
import com.permitseoul.permitserver.domain.reservationsession.core.repository.ReservationSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationSessionRetriever {
    private final ReservationSessionRepository reservationSessionRepository;

    @Transactional(readOnly = true)
    public ReservationSession getValidatedReservationSession(final long userId, final String sessionKey, final LocalDateTime now) {
        final ReservationSessionEntity reservationSession = reservationSessionRepository.findValidSessionByUserIdAndSessionKeyAndValidTime(
                userId,
                sessionKey,
                now
        ).orElseThrow(ReservationSessionNotFoundException::new);
        return ReservationSession.fromEntity(reservationSession);
    }

    @Transactional(readOnly = true)
    public ReservationSessionEntity getValidReservationSessionEntity(final long userId, final String sessionKey, final LocalDateTime now) {
        return reservationSessionRepository.findValidSessionByUserIdAndSessionKeyAndValidTime(
                userId,
                sessionKey,
                now
        ).orElseThrow(ReservationSessionNotFoundException::new);
    }

    public ReservationSessionEntity findReservationSessionEntityById(final long reservationSessionId) {
        return reservationSessionRepository.findById(reservationSessionId).orElseThrow(
                ReservationSessionNotFoundException::new
        );
    }
}
