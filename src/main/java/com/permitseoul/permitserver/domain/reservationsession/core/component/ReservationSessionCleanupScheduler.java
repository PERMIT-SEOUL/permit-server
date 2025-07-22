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

        // 1. 성공 세션은 시간 상관없이 전부 삭제
        final List<ReservationSessionEntity> successSessions = reservationSessionRepository.findAllBySuccessfulTrue();
        reservationSessionRepository.deleteAllInBatch(successSessions);

        // 2. 실패 + 7분 경과 세션만 따로 조회
        final List<ReservationSessionEntity> expiredOrFailedSessions = reservationSessionRepository.findAllBySuccessfulFalseAndCreatedAtBefore(expireThreshold);

        // Redis rollback
        final Map<Long, Integer> rollbackMap = new HashMap<>();
        List<String> expiredOrderIds = expiredOrFailedSessions.stream()
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
