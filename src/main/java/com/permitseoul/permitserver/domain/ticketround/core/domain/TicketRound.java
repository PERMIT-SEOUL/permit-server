package com.permitseoul.permitserver.domain.ticketround.core.domain;

import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TicketRound {
    private final long ticketRoundId;
    private final long eventId;
    private final String ticketRoundTitle;
    private final LocalDateTime salesStartAt;
    private final LocalDateTime salesEndAt;

    public static TicketRound fromEntity(final TicketRoundEntity ticketRoundEntity) {
        return new TicketRound(
                ticketRoundEntity.getTicketRoundId(),
                ticketRoundEntity.getEventId(),
                ticketRoundEntity.getTicketRoundTitle(),
                ticketRoundEntity.getSalesStartAt(),
                ticketRoundEntity.getSalesEndAt()
        );
    }
}
