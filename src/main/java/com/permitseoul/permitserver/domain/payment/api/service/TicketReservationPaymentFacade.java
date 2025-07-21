package com.permitseoul.permitserver.domain.payment.api.service;

import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentResponse;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentSaver;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationUpdater;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionRetriever;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionUpdater;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSessionStatus;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketSaver;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeUpdater;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static com.permitseoul.permitserver.global.formatter.DateFormatterUtil.parseDateToLocalDateTime;

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
    private final PaymentSaver paymentSaver;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePaymentAndAllTickets(final List<Ticket> newTicketList,
                                         final List<ReservationTicket> reservationTicketList,
                                         final long reservationSessionId,
                                         final long reservationId,
                                         final TossPaymentResponse tossPaymentResponse) {

        final ReservationSessionEntity reservationSessionEntity = reservationSessionRetriever.findReservationSessionEntityById(reservationSessionId);
        final ReservationEntity reservationEntity = reservationRetriever.findReservationEntityById(reservationId);

        savePaymentInfo(reservationEntity, tossPaymentResponse);
        decreaseTicketCountAtTicketTypeDBWithLock(reservationTicketList);
        ticketSaver.saveTickets(newTicketList);
        updateReservationStatus(reservationEntity, ReservationStatus.TICKET_ISSUED);
        reservationSessionUpdater.updateReservationSessionStatus(reservationSessionEntity, ReservationSessionStatus.SUCCESS);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateReservationStatusAndTossResponseTime(final long reservationId, final ReservationStatus reservationStatus) {
        final ReservationEntity reservationEntity = reservationRetriever.findReservationEntityById(reservationId);
        updateReservationStatus(reservationEntity, reservationStatus);
        updateReservationTossResponseTime(reservationEntity);
    }

    private void updateReservationTossResponseTime(final ReservationEntity reservationEntity) {
        final LocalDateTime now = LocalDateTime.now();
        reservationUpdater.updateTossResponseTime(reservationEntity, now);
    }

    private void savePaymentInfo(final ReservationEntity reservation, final TossPaymentResponse tossPaymentResponse) {
        paymentSaver.savePayment(
                reservation.getReservationId(),
                reservation.getOrderId(),
                reservation.getEventId(),
                tossPaymentResponse.paymentKey(),
                reservation.getTotalAmount(),
                tossPaymentResponse.currency(),
                parseDateToLocalDateTime(tossPaymentResponse.requestedAt()),
                parseDateToLocalDateTime(tossPaymentResponse.approvedAt())
        );
    }
    private void decreaseTicketCountAtTicketTypeDBWithLock(final List<ReservationTicket> reservationTicketList) {
        reservationTicketList.stream()
                .sorted(Comparator.comparing(ReservationTicket::getTicketTypeId))
                .forEach( reservationTicket -> {
                    final TicketTypeEntity ticketTypeEntity = ticketTypeRetriever.findByIdWithLock(reservationTicket.getTicketTypeId());
                    ticketTypeUpdater.decreaseTicketCount(ticketTypeEntity, reservationTicket.getCount());
                } );
    }

    private void updateReservationStatus(final ReservationEntity reservationEntity, final ReservationStatus reservationStatus) {
        reservationUpdater.updateReservationStatus(reservationEntity, reservationStatus);
    }
}
