package com.permitseoul.permitserver.domain.ticket.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tickets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TicketEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "ticket_type_id", nullable = false)
    private long ticketTypeId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "ticket_code", nullable = false)
    private String ticketCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed;

    @Builder
    public TicketEntity(final long userId,
                        final String orderId,
                        final long ticketTypeId,
                        final long eventId,
                        final String ticketCode
                        ) {
        this.userId = userId;
        this.orderId = orderId;
        this.ticketTypeId = ticketTypeId;
        this.eventId = eventId;
        this.ticketCode = ticketCode;
        this.status = TicketStatus.RESERVED;
        this.isUsed = false;
    }
}

