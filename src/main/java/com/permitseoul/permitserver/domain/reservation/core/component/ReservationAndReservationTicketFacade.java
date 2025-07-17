package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoRequest;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationAndReservationTicketFacade {

    private final ReservationSaver reservationSaver;
    private final ReservationTicketSaver reservationTicketSaver;

    @Transactional
    public String saveReservationWithTicket(final long userId,
                                          final long eventId,
                                          final String orderId,
                                          final BigDecimal totalAmount,
                                          final String couponCode,
                                          final List<ReservationInfoRequest.TicketTypeInfo> requestTicketTypeInfos) {
        final Reservation reservation = reservationSaver.saveReservation(userId, eventId, orderId, totalAmount, couponCode);
        requestTicketTypeInfos.forEach(
                ticketTypeInfo -> reservationTicketSaver.saveReservationTicket(ticketTypeInfo.id(), reservation.getOrderId(), ticketTypeInfo.count())
        );
        return reservation.getOrderId();
    }
}
