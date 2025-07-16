package com.permitseoul.permitserver.domain.payment.api.service;

import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationUpdater;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketSaver;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeUpdater;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketReservationFacade {
    private final TicketSaver ticketSaver;
    private final ReservationUpdater reservationUpdater;
    private final TicketTypeRetriever ticketTypeRetriever;
    private final TicketTypeUpdater ticketTypeUpdater;

    @Transactional
    public void saveAllTickets(final List<Ticket> newTicketList,
                               final ReservationEntity reservationEntity,
                               final List<ReservationTicket> reservationTicketList) {
        ticketSaver.saveTickets(newTicketList);
        reservationUpdater.updateReservationStatus(reservationEntity, ReservationStatus.TICKET_ISSUED);
        reservationTicketList.forEach(
                reservationTicket -> {
                    final TicketTypeEntity ticketTypeEntity = ticketTypeRetriever.findTicketTypeEntityById(reservationTicket.getTicketTypeId());
                    ticketTypeUpdater.decreaseTicketCount(ticketTypeEntity, reservationTicket.getCount());
                }
        );
    }
}
