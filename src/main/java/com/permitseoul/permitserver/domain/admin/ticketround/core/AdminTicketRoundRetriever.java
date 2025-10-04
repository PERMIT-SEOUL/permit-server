package com.permitseoul.permitserver.domain.admin.ticketround.core;

import com.permitseoul.permitserver.domain.admin.ticketround.exception.AdminTicketRoundNotFoundException;
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
        return ticketRoundRepository.findAllByEventIdIn(eventIds).stream()
                .map(TicketRound::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketRound getTicketRoundById(final long ticketRoundId) {
        return TicketRound.fromEntity(ticketRoundRepository.findById(ticketRoundId).orElseThrow(AdminTicketRoundNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public List<TicketRound> getTicketRoundByEventId(final long eventId) {
        return ticketRoundRepository.findAllByEventId(eventId).stream()
                .map(TicketRound::fromEntity)
                .toList();
    }
}
