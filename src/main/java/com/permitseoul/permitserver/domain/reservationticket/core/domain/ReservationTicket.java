package com.permitseoul.permitserver.domain.reservationticket.core.domain;

import com.permitseoul.permitserver.domain.reservationticket.core.domain.entity.ReservationTicketEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationTicket {
    private final long reservationTicketId;
    private final long ticketTypeId;
    private final String orderId;
    private final int count;

    public static ReservationTicket fromEntity(final ReservationTicketEntity reservationTicketEntity) {
        return new ReservationTicket(
                reservationTicketEntity.getReservationTicketId(),
                reservationTicketEntity.getTicketTypeId(),
                reservationTicketEntity.getOrderId(),
                reservationTicketEntity.getCount());
    }
}
