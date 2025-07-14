package com.permitseoul.permitserver.domain.ticketround.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_rounds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketRoundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_round_id")
    private Long ticketRoundId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "ticket_round_title", nullable = false)
    private String ticketRoundTitle;

    @Column(name = "sales_start_date", nullable = false)
    private LocalDateTime saleStartDate;

    @Column(name = "sales_end_date", nullable = false)
    private LocalDateTime salesEndDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    private TicketRoundEntity(long eventId, String ticketRoundTitle, LocalDateTime saleStartDate, LocalDateTime salesEndDate, boolean isActive) {
        this.eventId = eventId;
        this.ticketRoundTitle = ticketRoundTitle;
        this.saleStartDate = saleStartDate;
        this.salesEndDate = salesEndDate;
        this.isActive = isActive;
    }
}
