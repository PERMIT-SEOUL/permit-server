package com.permitseoul.permitserver.domain.admin.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class AdminTicketTypeUpdater {

    public void updateTicketType(final TicketTypeEntity ticketTypeEntity,
                                 final String name,
                                 final BigDecimal price,
                                 final int totalCount,
                                 final LocalDateTime startAt,
                                 final LocalDateTime endAt) {
        ticketTypeEntity.update(name, price, totalCount, startAt, endAt);
    }
}
