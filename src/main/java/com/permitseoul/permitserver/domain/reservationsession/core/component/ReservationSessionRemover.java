package com.permitseoul.permitserver.domain.reservationsession.core.component;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import com.permitseoul.permitserver.domain.reservationsession.core.repository.ReservationSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationSessionRemover {
    private final ReservationSessionRepository reservationSessionRepository;

    public void deleteAllInBatch(final List<ReservationSessionEntity> reservationSessionEntities) {
        reservationSessionRepository.deleteAllInBatch(reservationSessionEntities);
    }

    @Transactional
    public void deleteByOrderId(final String orderId) {
        reservationSessionRepository.deleteByOrderId(orderId);
    }
}
