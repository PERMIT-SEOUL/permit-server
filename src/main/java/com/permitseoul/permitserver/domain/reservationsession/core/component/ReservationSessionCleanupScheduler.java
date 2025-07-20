package com.permitseoul.permitserver.domain.reservationsession.core.component;

import com.permitseoul.permitserver.domain.reservationsession.core.repository.ReservationSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationSessionCleanupScheduler {

    private final ReservationSessionRepository reservationSessionRepository;

    @Scheduled(cron = "*/10 * * * * *") // 매 1분마다 실행
    @Transactional
    public void cleanupOldOrSuccessfulSessions() {
        final LocalDateTime expireThreshold = LocalDateTime.now().minusMinutes(7);

        int deletedCount = reservationSessionRepository.deleteBySuccessfulTrueOrCreatedAtBefore(expireThreshold);

        log.info("[Scheduler] Deleted {} old/successful reservation sessions", deletedCount);
    }
}
