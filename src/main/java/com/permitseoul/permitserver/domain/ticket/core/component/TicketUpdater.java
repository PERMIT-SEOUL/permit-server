package com.permitseoul.permitserver.domain.ticket.core.component;

import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TicketUpdater {

    @Transactional
    public void updateTicketStatus(final TicketEntity ticketEntity, final TicketStatus status) {
        ticketEntity.updateTicketStatus(status);;
    }
}
