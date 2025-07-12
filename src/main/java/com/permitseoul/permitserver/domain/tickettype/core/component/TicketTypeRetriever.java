package com.permitseoul.permitserver.domain.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketTypeRetriever {
    private final TicketTypeRepository ticketTypeRepository;

    public TicketTypeEntity findTicketTypeEntityById(final long ticketTypeId) {
        return ticketTypeRepository.findById(ticketTypeId).orElseThrow(TicketTypeNotfoundException::new);
    }
}
