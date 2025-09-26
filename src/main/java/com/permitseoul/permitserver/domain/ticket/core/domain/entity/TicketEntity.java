package com.permitseoul.permitserver.domain.ticket.core.domain.entity;

import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "used_time")
    private LocalDateTime usedTime;

    private TicketEntity(final long userId,
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
    }

    public static TicketEntity create(final long userId, final String orderId, final long ticketTypeId, final long eventId, final String ticketCode) {
        return new TicketEntity(userId, orderId, ticketTypeId, eventId, ticketCode);
    }

    public void updateTicketStatus(final TicketStatus status) {
        this.status = status;
        if(status == TicketStatus.USED) {
            this.usedTime = LocalDateTime.now();
        }
    }
}

