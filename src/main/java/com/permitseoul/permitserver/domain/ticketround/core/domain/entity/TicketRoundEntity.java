package com.permitseoul.permitserver.domain.ticketround.core.domain.entity;

import com.permitseoul.permitserver.domain.ticketround.core.exception.TicketRoundExpiredException;
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
    private LocalDateTime salesStartDate;

    @Column(name = "sales_end_date", nullable = false)
    private LocalDateTime salesEndDate;

    private TicketRoundEntity(long eventId, String ticketRoundTitle, LocalDateTime salesStartDate, LocalDateTime salesEndDate) {
        this.eventId = eventId;
        this.ticketRoundTitle = ticketRoundTitle;
        this.salesStartDate = salesStartDate;
        this.salesEndDate = salesEndDate;
    }

    public void verifyTicketSalesAvailable(final LocalDateTime now) {
        if (now.isBefore(this.salesStartDate) || now.isAfter(this.salesEndDate)) {
            throw new TicketRoundExpiredException();
        }
    }
}
