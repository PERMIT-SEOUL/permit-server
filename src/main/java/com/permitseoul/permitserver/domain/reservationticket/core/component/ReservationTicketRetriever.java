package com.permitseoul.permitserver.domain.reservationticket.core.component;

import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.entity.ReservationTicketEntity;
import com.permitseoul.permitserver.domain.reservationticket.core.exception.ReservationTicketNotFoundException;
import com.permitseoul.permitserver.domain.reservationticket.core.repository.ReservationTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ReservationTicketRetriever {
    private final ReservationTicketRepository reservationTicketRepository;

    public List<ReservationTicket> findAllByOrderId(final String orderId) {
        final List<ReservationTicketEntity> reservationTicketEntities = reservationTicketRepository.findAllByOrderId(orderId);
        if (reservationTicketEntities == null || reservationTicketEntities.isEmpty()) {
            throw new ReservationTicketNotFoundException();
        }
        return reservationTicketEntities.stream().map(
                ReservationTicket::fromEntity
        ).toList();
    }
}
