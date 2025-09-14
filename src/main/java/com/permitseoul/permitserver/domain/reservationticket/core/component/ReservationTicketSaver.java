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

    public void saveReservationTicket(final long ticketTypeId,
                                                   final String orderId,
                                                   final int count) {
        reservationTicketRepository.save(ReservationTicketEntity.create(ticketTypeId, orderId, count));
    }
}
