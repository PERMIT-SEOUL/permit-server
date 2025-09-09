package com.permitseoul.permitserver.domain.admin.ticketround.core;

import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import com.permitseoul.permitserver.domain.ticketround.core.repository.TicketRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminTicketRoundSaver {
    private final TicketRoundRepository ticketRoundRepository;

    public TicketRound saveTicketRound(final long eventId,
                                       final String ticketRoundTitle,
                                       final LocalDateTime salesStartDate,
                                       final LocalDateTime salesEndDate ) {
        return TicketRound.fromEntity(
                ticketRoundRepository.save(TicketRoundEntity.create(eventId, ticketRoundTitle, salesStartDate, salesEndDate))
        );
    }
}
