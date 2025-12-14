package com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.GuestTicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "guests_tickets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuestTicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name = "guest_ticket_id")
    private Long guestTicketId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "guest_id", nullable = false)
    private long guestId;

    @Column(name = "guest_ticket_code", nullable = false)
    @Getter
    private String guestTicketCode;

    @Column(name = "guest_ticket_status")
    @Enumerated(EnumType.STRING)
    private GuestTicketStatus status;

    @Column(name = "used_time")
    private LocalDateTime usedTime;

    private GuestTicketEntity(long eventId, long guestId, String guestTicketCode) {
        this.eventId = eventId;
        this.guestId = guestId;
        this.guestTicketCode = guestTicketCode;
        this.status = GuestTicketStatus.ISSUED;
    }

    public static GuestTicketEntity create(final long eventId,
                                           final long guestId,
                                           final String guestTicketCode) {
        return new GuestTicketEntity(eventId, guestId, guestTicketCode);
    }

    public void updateStatus(final GuestTicketStatus guestTicketStatus) {
        this.status = guestTicketStatus;
    }

}
