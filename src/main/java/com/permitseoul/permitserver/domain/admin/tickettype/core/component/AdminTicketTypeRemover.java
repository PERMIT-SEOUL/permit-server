package com.permitseoul.permitserver.domain.admin.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTicketTypeRemover {
    private final TicketTypeRepository ticketTypeRepository;

    public void deleteAllTicketTypeByTicketRoundId(final long ticketRoundId) {
        ticketTypeRepository.deleteAllByTicketRoundId(ticketRoundId);
    }

    public void deleteTicketTypeById(final long ticketTypeId) {
        ticketTypeRepository.deleteById(ticketTypeId);
    }
}
