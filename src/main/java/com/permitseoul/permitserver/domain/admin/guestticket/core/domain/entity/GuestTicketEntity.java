package com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guests_tickets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuestTicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_ticket_id")
    private Long guestTicketId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "guest_id", nullable = false)
    private long guestId;

    @Column(name = "guest_ticket_code", nullable = false)
    @Getter
    private String guestTicketCode;

    @Column(name = "usable")
    private boolean usable;

    private GuestTicketEntity(long eventId, long guestId, String guestTicketCode) {
        this.eventId = eventId;
        this.guestId = guestId;
        this.guestTicketCode = guestTicketCode;
        this.usable = true;
    }

    public static GuestTicketEntity create(final long eventId,
                                           final long guestId,
                                           final String guestTicketCode) {
        return new GuestTicketEntity(eventId, guestId, guestTicketCode);
    }
}
