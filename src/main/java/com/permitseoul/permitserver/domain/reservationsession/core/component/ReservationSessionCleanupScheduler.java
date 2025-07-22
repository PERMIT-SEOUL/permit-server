package com.permitseoul.permitserver.domain.reservationsession.core.component;

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
    private final ReservationTicketRetriever reservationTicketRetriever;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "*/30 * * * * *") // 매 30초마다
    @Transactional
    public void cleanupSessions() {
        final LocalDateTime expireThreshold = LocalDateTime.now().minusMinutes(7);

        // 성공인 세션들 -> 모두 삭제
        final List<ReservationSessionEntity> successSessions = reservationSessionRepository.findAllBySuccessfulTrue();
        reservationSessionRepository.deleteAllInBatch(successSessions);

        // 실패인 세션들 + 7분 지난 세션 -> 롤백 및 삭제
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
                redisTemplate.opsForValue().increment(redisKey, count);
//                log.info("[Scheduler] Redis rollback: ticketTypeId={}, count={}", ticketTypeId, count);
            });
        }
        reservationSessionRepository.deleteAllInBatch(expiredOrFailedSessions);
//        log.info("[Scheduler] Deleted {} expired & unsuccessful sessions", expiredOrFailedSessions.size());
    }
}
