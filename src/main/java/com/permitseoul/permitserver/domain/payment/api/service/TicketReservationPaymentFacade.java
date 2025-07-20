package com.permitseoul.permitserver.domain.payment.api.service;

import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationUpdater;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionRetriever;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionUpdater;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketSaver;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeUpdater;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketReservationPaymentFacade {
    private final TicketSaver ticketSaver;
    private final ReservationUpdater reservationUpdater;
    private final TicketTypeRetriever ticketTypeRetriever;
    private final TicketTypeUpdater ticketTypeUpdater;
    private final ReservationSessionUpdater reservationSessionUpdater;
    private final ReservationSessionRetriever reservationSessionRetriever;
    private final ReservationRetriever reservationRetriever;

    @Transactional
    public void savePaymentAndAllTickets(final List<Ticket> newTicketList,
                                         final List<ReservationTicket> reservationTicketList,
                                         final long userId,
                                         final String reservationSessionKey,
                                         final BigDecimal totalAmount) {
        final ReservationSessionEntity reservationSessionEntity = getReservationSession(userId, reservationSessionKey);
        final ReservationEntity reservationEntity = reservationRetriever.findReservationByOrderIdAndAmountAndUserId(reservationSessionEntity.getOrderId(), totalAmount, userId);


        decreaseTicketCountAtTicketTypeDBWithLock(reservationTicketList);
        ticketSaver.saveTickets(newTicketList);
        reservationUpdater.updateReservationStatus(reservationEntity, ReservationStatus.TICKET_ISSUED);
        reservationSessionUpdater.updateReservationSessionToSuccessful(reservationSessionEntity);
    }

    @Transactional
    public void updateReservationStatusAndTossResponseTime(final long reservationId, final ReservationStatus reservationStatus) {

    }

    private void decreaseTicketCountAtTicketTypeDBWithLock(final List<ReservationTicket> reservationTicketList) {
        reservationTicketList.stream()
                .sorted(Comparator.comparing(ReservationTicket::getTicketTypeId))
                .forEach( reservationTicket -> {
                    final TicketTypeEntity ticketTypeEntity = ticketTypeRetriever.findByIdWithLock(reservationTicket.getTicketTypeId());
                    ticketTypeUpdater.decreaseTicketCount(ticketTypeEntity, reservationTicket.getCount());
                } );
    }

    private ReservationSessionEntity getReservationSession(final long userId, final String sessionKey) {
        final LocalDateTime validTime = LocalDateTime.now().minusMinutes(7);
        return reservationSessionRetriever.getValidatedReservationSessionEntity(userId, sessionKey, validTime);
    }
}
