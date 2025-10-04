package com.permitseoul.permitserver.domain.ticket.core.domain;

import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class Ticket {
    private final long ticketId;
    private final long userId;
    private final String orderId;
    private final long ticketTypeId;
    private final long eventId;
    private final String ticketCode;
    private final TicketStatus status;
    private final LocalDateTime createdAt;
    private final BigDecimal ticketPrice;

    public static Ticket fromEntity(final TicketEntity entity) {
        return new Ticket(
                entity.getTicketId(),
                entity.getUserId(),
                entity.getOrderId(),
                entity.getTicketTypeId(),
                entity.getEventId(),
                entity.getTicketCode(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getTicketPrice()
        );
    }
}

