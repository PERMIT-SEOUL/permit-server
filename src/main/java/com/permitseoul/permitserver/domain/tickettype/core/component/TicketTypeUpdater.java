package com.permitseoul.permitserver.domain.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TicketTypeUpdater {

    @Transactional
    public void decreaseTicketCount(@Positive final TicketTypeEntity ticketTypeEntity, final int count) {
        ticketTypeEntity.decreaseTicketCount(count);
    }

    @Transactional
    public void increaseTicketCount(@Positive final TicketTypeEntity ticketTypeEntity, final int count) {
        ticketTypeEntity.increaseTicketCount(count);
    }
}
