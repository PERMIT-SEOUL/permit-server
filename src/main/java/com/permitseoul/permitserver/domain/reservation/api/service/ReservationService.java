package com.permitseoul.permitserver.domain.reservation.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.reservation.api.dto.PaymentReadyRequest;
import com.permitseoul.permitserver.domain.reservation.api.dto.PaymentReadyResponse;
import com.permitseoul.permitserver.domain.reservation.api.exception.NotfoundReservationException;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationSaver;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketSaver;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationSaver reservationSaver;
    private final ReservationTicketSaver reservationTicketSaver;
    private final EventRetriever eventRetriever;
    private final UserRetriever userRetriever;

    @Transactional
    public PaymentReadyResponse getPaymentReady(final long userId,
                                                final long eventId,
                                                final String couponCode,
                                                final int totalAmount,
                                                final String orderId,
                                                final List<PaymentReadyRequest.TicketTypeInfo> ticketTypeInfos) {
        final Event event;
        final User user;
        try {
            event = eventRetriever.getEvent(eventId);
            user = userRetriever.findUserById(userId);
        } catch (EventNotfoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_EVENT);
        } catch (UserNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_USER);
        }

        final Reservation reservation = reservationSaver.saveReservation(userId, eventId, orderId, totalAmount, couponCode, ReservationStatus.PENDING);
        ticketTypeInfos.forEach(
                ticketTypeInfo -> reservationTicketSaver.saveReservationTicket(ticketTypeInfo.id(), reservation.getOrderId(), ticketTypeInfo.count())
        );

        return PaymentReadyResponse.of(event.getName(), reservation.getOrderId(), user.getName(), user.getEmail(), reservation.getTotalAmount(), user.getSocialId());
    }

}
