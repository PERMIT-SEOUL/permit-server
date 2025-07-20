package com.permitseoul.permitserver.domain.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TicketTypeUpdater {

    @Transactional
    public void decreaseTicketCount(final TicketTypeEntity ticketTypeEntity, final int count) {
        ticketTypeEntity.decreaseTicketCount(count);
    }

    @Transactional
    public void increaseTicketCount(final TicketTypeEntity ticketTypeEntity, final int count) {
        ticketTypeEntity.increaseTicketCount(count);
    }
}
