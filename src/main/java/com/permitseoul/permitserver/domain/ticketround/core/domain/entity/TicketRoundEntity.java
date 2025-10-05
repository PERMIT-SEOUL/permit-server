package com.permitseoul.permitserver.domain.ticketround.core.domain.entity;

import com.permitseoul.permitserver.domain.ticketround.core.exception.TicketRoundExpiredException;
import com.permitseoul.permitserver.domain.ticketround.core.exception.TicketRoundIllegalArgumentException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
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

    @Column(name = "sales_start_at", nullable = false)
    private LocalDateTime salesStartAt;

    @Column(name = "sales_end_at", nullable = false)
    private LocalDateTime salesEndAt;

    private TicketRoundEntity(long eventId, String ticketRoundTitle, LocalDateTime salesStartAt, LocalDateTime salesEndAt) {
        validateDateTIme(salesStartAt, salesEndAt);

        this.eventId = eventId;
        this.ticketRoundTitle = ticketRoundTitle;
        this.salesStartAt = salesStartAt;
        this.salesEndAt = salesEndAt;
    }

    public static TicketRoundEntity create(final long eventId,
                                           final String ticketRoundTitle,
                                           final LocalDateTime salesStartAt,
                                           final LocalDateTime salesEndAt) {
        return new TicketRoundEntity(eventId, ticketRoundTitle, salesStartAt, salesEndAt);
    }

    public void update(final String ticketRoundName,
                       final LocalDateTime ticketRoundSalesStartDate,
                       final LocalDateTime ticketRoundSalesEndDate) {
        validateDateTIme(ticketRoundSalesStartDate, ticketRoundSalesEndDate);
        this.ticketRoundTitle = ticketRoundName;
        this.salesStartAt = ticketRoundSalesStartDate;
        this.salesEndAt = ticketRoundSalesEndDate;
    }

    public void verifyTicketSalesAvailable(final LocalDateTime now) {
        if (now.isBefore(this.salesStartAt) || now.isAfter(this.salesEndAt)) {
            throw new TicketRoundExpiredException();
        }
    }

    private void validateDateTIme(final LocalDateTime salesStartAt, final LocalDateTime salesEndAt) {
        if (salesStartAt.isAfter(salesEndAt)) {
            throw new TicketRoundIllegalArgumentException();
        }
    }
}
