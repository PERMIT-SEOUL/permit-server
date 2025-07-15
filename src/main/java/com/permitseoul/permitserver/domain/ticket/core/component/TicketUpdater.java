package com.permitseoul.permitserver.domain.ticket.core.component;

import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketUpdater {

    public void updateTicketStatus(final TicketEntity ticketEntity, final TicketStatus status) {
        ticketEntity.updateTicketStatus(status);;
    }
}
