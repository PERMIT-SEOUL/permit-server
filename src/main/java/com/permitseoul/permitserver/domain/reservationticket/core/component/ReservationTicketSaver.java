package com.permitseoul.permitserver.domain.reservationticket.core.component;

import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.entity.ReservationTicketEntity;
import com.permitseoul.permitserver.domain.reservationticket.core.repository.ReservationTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationTicketSaver {
    private final ReservationTicketRepository reservationTicketRepository;

    public ReservationTicket saveReservationTicket(final long ticketTypeId,
                                                   final String orderId,
                                                   final int count) {
        final ReservationTicketEntity reservationTicketEntity = reservationTicketRepository.save(ReservationTicketEntity.create(ticketTypeId, orderId, count));
        return ReservationTicket.fromEntity(reservationTicketEntity);
    }
}
