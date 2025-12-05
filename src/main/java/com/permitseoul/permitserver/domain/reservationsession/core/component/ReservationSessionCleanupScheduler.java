package com.permitseoul.permitserver.domain.reservationsession.core.component;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.SessionProperties;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import com.permitseoul.permitserver.domain.reservationsession.core.repository.ReservationSessionRepository;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketRetriever;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.global.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationSessionCleanupScheduler {

    private final ReservationSessionRepository reservationSessionRepository;
    private final ReservationSessionRemover reservationSessionRemover;
    private final ReservationTicketRetriever reservationTicketRetriever;
    private final RedisTemplate<String, String> redisTemplate;
    private final SessionProperties sessionProperties;

    @Scheduled(cron = "0 * * * * *") // 매 1분마다
    @Transactional
    public void cleanupSessions() {
        final LocalDateTime expireThreshold = LocalDateTime.now().minusMinutes(sessionProperties.expireTime());

        log.error("TESTTTTTTTTTTTTTTTTTTTT"); //todo: test용, 추후 삭제

        // 성공인 세션들 -> 모두 삭제
        final List<ReservationSessionEntity> successSessions = reservationSessionRepository.findAllBySuccessfulTrue();
        reservationSessionRemover.deleteAllInBatch(successSessions);

        // 7분 지난 세션 -> 롤백 및 삭제
        final List<ReservationSessionEntity> expiredOrFailedSessions = reservationSessionRepository.findAllBySuccessfulFalseAndCreatedAtBefore(expireThreshold);
        final Map<Long, Integer> rollbackMap = new HashMap<>();
        final List<String> expiredOrderIds = expiredOrFailedSessions.stream()
                .map(ReservationSessionEntity::getOrderId)
                .toList();

        if (!expiredOrderIds.isEmpty()) {
            final List<ReservationTicket> reservationTickets = reservationTicketRetriever.findAllByOrderIds(expiredOrderIds);
            reservationTickets.forEach(
                    ticket -> rollbackMap.merge(ticket.getTicketTypeId(), ticket.getCount(), Integer::sum)
            );

            rollbackMap.forEach((ticketTypeId, count) -> {
                final String redisKey = Constants.REDIS_TICKET_TYPE_KEY_NAME + ticketTypeId + Constants.REDIS_TICKET_TYPE_REMAIN;
                try {
                    redisTemplate.opsForValue().increment(redisKey, count);
                    log.info("[Scheduler] Redis rollback: ticketTypeId={}, count={}", ticketTypeId, count);
                } catch (Exception e) {
                    log.error("[Scheduler] Redis rollback failed: ticketTypeId={}, count={}", ticketTypeId, count, e); // 실패한 롤백들 알림 발송해야될듯
                }
            });
        }
        reservationSessionRepository.deleteAllInBatch(expiredOrFailedSessions);
    }
}
