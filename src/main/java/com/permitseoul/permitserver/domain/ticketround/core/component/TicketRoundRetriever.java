package com.permitseoul.permitserver.domain.ticketround.core.component;

import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import com.permitseoul.permitserver.domain.ticketround.core.exception.TicketRoundNotFoundException;
import com.permitseoul.permitserver.domain.ticketround.core.repository.TicketRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TicketRoundRetriever {
    private final TicketRoundRepository ticketRoundRepository;

    @Transactional(readOnly = true)
    public TicketRoundEntity findTicketRoundEntityById(final long ticketRoundId) {
        return ticketRoundRepository.findById(ticketRoundId).orElseThrow(TicketRoundNotFoundException::new);
    }
}
