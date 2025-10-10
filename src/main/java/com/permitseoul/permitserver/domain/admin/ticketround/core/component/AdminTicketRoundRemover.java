package com.permitseoul.permitserver.domain.admin.ticketround.core.component;

import com.permitseoul.permitserver.domain.ticketround.core.repository.TicketRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTicketRoundRemover {
    private final TicketRoundRepository ticketRoundRepository;

    public void deleteTicketRoundById(final long ticketRoundId) {
        ticketRoundRepository.deleteById(ticketRoundId);
    }
}
