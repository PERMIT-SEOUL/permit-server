package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoRequest;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionSaver;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationAndReservationTicketFacade {

    private final ReservationSaver reservationSaver;
    private final ReservationTicketSaver reservationTicketSaver;
    private final ReservationSessionSaver reservationSessionSaver;

    @Transactional
    public String saveReservationWithTicketAndSessionKey(final long userId,
                                                         final long eventId,
                                                         final String orderId,
                                                         final BigDecimal totalAmount,
                                                         final String couponCode,
                                                         final List<ReservationInfoRequest.TicketTypeInfo> requestTicketTypeInfos) {
        final Reservation reservation = reservationSaver.saveReservation(userId, eventId, orderId, totalAmount, couponCode);
        requestTicketTypeInfos.forEach(
                ticketTypeInfo -> reservationTicketSaver.saveReservationTicket(ticketTypeInfo.id(), reservation.getOrderId(), ticketTypeInfo.count())
        );

        //세션 생성
        final String sessionKey = UUID.randomUUID().toString();
        final ReservationSession reservationSession = reservationSessionSaver.saveReservationSession(userId, orderId, sessionKey);
        return reservationSession.getSessionKey();
    }
}
