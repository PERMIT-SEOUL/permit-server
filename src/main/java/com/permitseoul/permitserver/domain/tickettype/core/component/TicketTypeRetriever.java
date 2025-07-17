package com.permitseoul.permitserver.domain.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TicketTypeRetriever {
    private final TicketTypeRepository ticketTypeRepository;

    @Transactional(readOnly = true)
    public TicketTypeEntity findTicketTypeEntityById(final long ticketTypeId) {
        return ticketTypeRepository.findById(ticketTypeId).orElseThrow(TicketTypeNotfoundException::new);
    }

    @Transactional(readOnly = true)
    public void validExistTicketType(final long ticketTypeId) {
        if(!ticketTypeRepository.existsById(ticketTypeId)) {
            throw new TicketTypeNotfoundException();
        }
    }

    @Transactional(readOnly = true)
    public void verifyTicketCount(final TicketTypeEntity ticketTypeEntity, final int count) {
        ticketTypeEntity.verifyTicketCount(count);
    }
}
