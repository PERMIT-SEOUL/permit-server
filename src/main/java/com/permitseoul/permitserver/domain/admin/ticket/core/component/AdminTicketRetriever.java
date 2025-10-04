package com.permitseoul.permitserver.domain.admin.ticket.core.component;

import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTicketRetriever {
    private final TicketRepository ticketRepository;

    // 특정 티켓타입의 판매 수량 조회 (RESERVED + USED 등)
    public long getSoldCount(final long ticketTypeId, final List<TicketStatus> statuses) {
        return ticketRepository.countByTicketTypeIdAndStatusIn(ticketTypeId, statuses);
    }

    // 특정 티켓타입의 판매 금액 합계 조회 (RESERVED + USED 등)
    public BigDecimal getSoldAmount(final long ticketTypeId, final List<TicketStatus> statuses) {
        return ticketRepository.sumTicketPriceByTicketTypeIdAndStatuses(ticketTypeId, statuses);
    }

    // 특정 티켓타입의 상태별 수량 조회 (CANCELED, USED 등)
    public long getCountByStatus(final long ticketTypeId, final TicketStatus status) {
        return ticketRepository.countByTicketTypeIdAndStatus(ticketTypeId, status);
    }
}
