package com.permitseoul.permitserver.domain.admin.ticketround.core.component;

import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class AdminTicketRoundUpdater {

    @Transactional
    public void updateTicketRound(final TicketRoundEntity ticketRoundEntity,
                                  final String ticketRoundName,
                                  final LocalDateTime ticketRoundSalesStartDate,
                                  final LocalDateTime ticketRoundSalesEndDate) {
        ticketRoundEntity.update(ticketRoundName, ticketRoundSalesStartDate, ticketRoundSalesEndDate);
    }
}
