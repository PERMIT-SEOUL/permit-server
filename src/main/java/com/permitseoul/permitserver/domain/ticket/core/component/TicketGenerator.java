package com.permitseoul.permitserver.domain.ticket.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.global.TicketOrCouponCodeGenerator;

import java.util.List;
import java.util.stream.IntStream;

public abstract class TicketGenerator {

    public static List<Ticket> generatePublicTickets(final List<ReservationTicket> reservationTicketList, final long userId, final Reservation reservation) {
        return reservationTicketList.stream()
                .flatMap(reservationTicket ->
                        IntStream.range(0, reservationTicket.getCount())
                                .mapToObj(i -> Ticket.builder()
                                        .userId(userId)
                                        .orderId(reservationTicket.getOrderId())
                                        .ticketTypeId(reservationTicket.getTicketTypeId())
                                        .eventId(reservation.getEventId())
                                        .ticketCode(TicketOrCouponCodeGenerator.generateCode())
                                        .status(TicketStatus.RESERVED)
                                        .build()
                                )
                )
                .toList();
    }

}
