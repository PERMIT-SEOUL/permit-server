package com.permitseoul.permitserver.domain.admin.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTicketTypeSaver {
    private final TicketTypeRepository ticketTypeRepository;

    public void saveAllTicketTypes(final List<TicketTypeEntity> ticketTypeEntityList) {
        ticketTypeRepository.saveAll(ticketTypeEntityList);
    }
}
