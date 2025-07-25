package com.permitseoul.permitserver.domain.ticketround.core.domain;

import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TicketRound {
    private final Long ticketRoundId;
    private final long eventId;
    private final String ticketRoundTitle;
    private final LocalDateTime salesStartDate;
    private final LocalDateTime salesEndDate;

    public static TicketRound fromEntity(final TicketRoundEntity ticketRoundEntity) {
        return new TicketRound(
                ticketRoundEntity.getTicketRoundId(),
                ticketRoundEntity.getEventId(),
                ticketRoundEntity.getTicketRoundTitle(),
                ticketRoundEntity.getSalesStartDate(),
                ticketRoundEntity.getSalesEndDate()
        );
    }
}
