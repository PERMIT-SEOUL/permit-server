package com.permitseoul.permitserver.domain.guestticket.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guests_tickets")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class GuestTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_ticket_id")
    private Long guestTicketId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "guest_id", nullable = false)
    private long guestId;

    @Column(name = "guest_ticket_code", nullable = false)
    private String guestTicketCode;

}
