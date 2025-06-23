package com.permitseoul.permit_server.ticket.entity;

import com.permitseoul.permit_server.reservation.entity.Reservation;
import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "reservation_id", nullable = false)
    private long reservationId;

    @Column(name = "ticket_code", nullable = false)
    private String ticketCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(name = "ticket_seat", nullable = false)
    private String ticketSeat;

}

