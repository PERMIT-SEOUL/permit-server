package com.permitseoul.permitserver.domain.payment.api.service;

import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationUpdater;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketSaver;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketReservationFacade {
    private final TicketSaver ticketSaver;
    private final ReservationUpdater reservationUpdater;

    @Transactional
    public void saveAllTickets(final List<Ticket> newTicketList, final ReservationEntity reservationEntity) {
        ticketSaver.saveTickets(newTicketList);
        reservationUpdater.updateReservationStatus(reservationEntity, ReservationStatus.TICKET_ISSUED);
    }
}
