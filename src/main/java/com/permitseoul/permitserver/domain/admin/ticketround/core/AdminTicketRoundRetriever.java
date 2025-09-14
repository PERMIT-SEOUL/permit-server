package com.permitseoul.permitserver.domain.admin.ticketround.core;

import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.ticketround.core.repository.TicketRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTicketRoundRetriever {
    private final TicketRoundRepository ticketRoundRepository;

    @Transactional(readOnly = true)
    public List<TicketRound> getTicketRoundsByEventIds(final List<Long> eventIds) {
        return ticketRoundRepository.findByEventIdIn(eventIds).stream()
                .map(TicketRound::fromEntity)
                .toList();
    }
}
