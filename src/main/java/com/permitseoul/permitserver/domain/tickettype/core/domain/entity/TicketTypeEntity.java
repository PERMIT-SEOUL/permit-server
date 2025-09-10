package com.permitseoul.permitserver.domain.tickettype.core.domain.entity;

import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeInsufficientCountException;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeTicketZeroException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "ticket_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private BigDecimal ticketPrice;

    @Column(name = "total_ticket_count", nullable = false)
    private int totalTicketCount;

    @Column(name = "remain_ticket_count", nullable = false)
    private int remainTicketCount;

    @Column(name = "ticket_start_at", nullable = false)
    private LocalDateTime ticketStartAt;

    @Column(name = "ticket_end_at", nullable = false)
    private LocalDateTime ticketEndAt;

    private TicketTypeEntity(long ticketRoundId,
                            String ticketTypeName,
                            BigDecimal ticketPrice,
                            int totalTicketCount,
                            LocalDateTime ticketStartAt,
                            LocalDateTime ticketEndAt) {
        this.ticketRoundId = ticketRoundId;
        this.ticketTypeName = ticketTypeName;
        this.ticketPrice = ticketPrice;
        this.totalTicketCount = totalTicketCount;
        this.remainTicketCount = totalTicketCount;
        this.ticketStartAt = ticketStartAt;
        this.ticketEndAt = ticketEndAt;
    }

    public static TicketTypeEntity create(final long ticketRoundId,
                                          final String ticketTypeName,
                                          final BigDecimal ticketPrice,
                                          final int totalTicketCount,
                                          final LocalDateTime ticketStartAt,
                                          final LocalDateTime ticketEndAt) {
        return new TicketTypeEntity(ticketRoundId, ticketTypeName, ticketPrice, totalTicketCount, ticketStartAt, ticketEndAt);
    }

    public void verifyTicketCount(final int buyTicketCount) {
        checkBuyTicketCountZero(buyTicketCount);
        if (this.remainTicketCount < buyTicketCount) {
            throw new TicketTypeInsufficientCountException();
        }
    }

    public void decreaseTicketCount(final int buyTicketCount) {
        checkBuyTicketCountZero(buyTicketCount);
        this.remainTicketCount -= buyTicketCount;
    }

    public void increaseTicketCount(final int buyTicketCount) {
        checkBuyTicketCountZero(buyTicketCount);
        this.remainTicketCount += buyTicketCount;
    }

    private void checkBuyTicketCountZero(final int buyTicketCount) {
        if ( buyTicketCount <= 0) {
            throw new TicketTypeTicketZeroException();
        }
    }
}
