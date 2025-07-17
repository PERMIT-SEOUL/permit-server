package com.permitseoul.permitserver.domain.tickettype.core.domain;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TicketType {
    private final Long ticketTypeId;
    private final long ticketRoundId;
    private final int ticketPrice;
    private final int totalTicketCount;
    private final int remainTicketCount;
    private final LocalDateTime ticketStartDate;
    private final LocalDateTime ticketEndDate;

    public static TicketType fromEntity(final TicketTypeEntity ticketTypeEntity) {
        return new TicketType(
                ticketTypeEntity.getTicketTypeId(),
                ticketTypeEntity.getTicketRoundId(),
                ticketTypeEntity.getTicketPrice(),
                ticketTypeEntity.getTotalTicketCount(),
                ticketTypeEntity.getRemainTicketCount(),
                ticketTypeEntity.getTicketStartDate(),
                ticketTypeEntity.getTicketEndDate()
        );
    }
}
