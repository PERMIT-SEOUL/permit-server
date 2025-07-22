package com.permitseoul.permitserver.domain.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
public class TicketTypeUpdater {

    public void decreaseTicketCount(final TicketTypeEntity ticketTypeEntity, @Positive(message = "count는 0이상이어야합니다.") final int count) {
        ticketTypeEntity.decreaseTicketCount(count);
    }

    @Transactional
    public void increaseTicketCount(final TicketTypeEntity ticketTypeEntity, @Positive(message = "count는 0이상이어야합니다.") final int count) {
        ticketTypeEntity.increaseTicketCount(count);
    }
}
