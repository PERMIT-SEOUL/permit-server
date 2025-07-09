package com.permitseoul.permitserver.domain.ticket.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import com.permitseoul.permitserver.domain.ticket.domain.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tickets")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
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

    @Column(name = "ticket_seat", nullable = false)
    private String ticketSeat;
}

