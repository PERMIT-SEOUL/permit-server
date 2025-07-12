package com.permitseoul.permitserver.domain.tickettype.core.domain.entity;

import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeInsufficientCountException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_type")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class TicketTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_type_id")
    private Long ticketTypeId;

    @Column(name = "ticket_round_id", nullable = false)
    private long ticketRoundId;

    @Column(name = "ticket_type_name")
    private String ticketTypeName;

    @Column(name = "ticket_price", nullable = false)
    private int ticketPrice;

    @Column(name = "total_ticket_count", nullable = false)
    private int totalTicketCount;

    @Column(name = "remain_ticket_count", nullable = false)
    private int remainTicketCount;

    @Column(name = "ticket_start_date", nullable = false)
    private LocalDateTime ticketStartDate;

    @Column(name = "ticket_end_date", nullable = false)
    private LocalDateTime ticketEndDate;


    public void decreaseRemainCount(final int buyTicketCount) {
        if (this.remainTicketCount < buyTicketCount) {
            throw new TicketTypeInsufficientCountException();
        }
        this.remainTicketCount -= buyTicketCount;
    }
}
