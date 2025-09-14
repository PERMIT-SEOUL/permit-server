package com.permitseoul.permitserver.domain.ticketround.core.repository;

import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface TicketRoundRepository extends JpaRepository<TicketRoundEntity, Long> {

    // 미래 라운드는 제외: salesStartDate <= now 인 라운드만(진행 중 + 종료된 라운드)
    List<TicketRoundEntity> findByEventIdAndSalesStartAtLessThanEqualOrderBySalesStartAtAsc(final long eventId,
                                                                                                final LocalDateTime now);

    List<TicketRoundEntity> findByEventIdIn(final List<Long> eventIds);
}
