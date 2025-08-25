package com.permitseoul.permitserver.domain.payment.api.service;

import com.permitseoul.permitserver.domain.coupon.core.component.CouponRetriever;
import com.permitseoul.permitserver.domain.coupon.core.component.CouponUpdater;
import com.permitseoul.permitserver.domain.coupon.core.domain.entity.CouponEntity;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentCancelResponse;
import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentResponse;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentSaver;
import com.permitseoul.permitserver.domain.paymentcancel.core.component.PaymentCancelSaver;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationUpdater;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionRetriever;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionUpdater;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketRetriever;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketRetriever;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketSaver;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketUpdater;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeUpdater;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import com.permitseoul.permitserver.global.util.DateFormatterUtil;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static com.permitseoul.permitserver.global.util.DateFormatterUtil.parseTossDateToLocalDateTime;

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
    private final TicketUpdater ticketUpdater;
    private final PaymentCancelSaver paymentCancelSaver;
    private final TicketRetriever ticketRetriever;
    private final ReservationTicketRetriever reservationTicketRetriever;
    private final CouponRetriever couponRetriever;
    private final CouponUpdater couponUpdater;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePaymentAndAllTickets(final List<Ticket> newTicketList,
                                         final List<ReservationTicket> reservationTicketList,
                                         final long reservationSessionId,
                                         final long reservationId,
                                         final TossPaymentResponse tossPaymentResponse) {

        final ReservationSessionEntity reservationSessionEntity = reservationSessionRetriever.findReservationSessionEntityByIdAfterPaymentSuccess(reservationSessionId);
        final ReservationEntity reservationEntity = reservationRetriever.findReservationEntityById(reservationId);

        savePaymentInfo(reservationEntity, tossPaymentResponse);
        decreaseTicketCountAtTicketTypeDBWithLock(reservationTicketList);
        ticketSaver.saveTickets(newTicketList);
        updateReservationStatus(reservationEntity, ReservationStatus.TICKET_ISSUED);
        reservationSessionUpdater.updateReservationSessionStatus(reservationSessionEntity);
        if (!(reservationEntity.getCouponCode() == null) && !(reservationEntity.getCouponCode().isEmpty())) {
            updateCouponToUsedTrue(reservationEntity.getCouponCode());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateReservationStatusAndTossResponseTime(final long reservationId, final ReservationStatus reservationStatus) {
        final ReservationEntity reservationEntity = reservationRetriever.findReservationEntityById(reservationId);
        updateReservationStatus(reservationEntity, reservationStatus);
        updateReservationTossResponseTime(reservationEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateTicketAndReservationStatus(final List<Long> ticketIdList,
                                                 final long reservationId,
                                                 final TicketStatus ticketStatus,
                                                 final ReservationStatus reservationStatus) {
        final List<TicketEntity> ticketEntityList = ticketRetriever.findAllTicketEntitiesById(ticketIdList);
        final ReservationEntity reservationEntity = reservationRetriever.findReservationEntityById(reservationId);

        updateTicketStatus(ticketEntityList, ticketStatus);
        updateReservationStatus(reservationEntity, reservationStatus);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseTicketTypeRemainCount(final String orderId) {
        final List<ReservationTicket> reservationTicketList = reservationTicketRetriever.findAllByOrderId(orderId);

        reservationTicketList.forEach(
                reservationTicket -> {
                    final TicketTypeEntity ticketTypeEntity = ticketTypeRetriever.findByIdWithLock(reservationTicket.getTicketTypeId());
                    ticketTypeUpdater.increaseTicketCount(ticketTypeEntity, reservationTicket.getCount());
                }
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCancelPayment(final List<PaymentCancelResponse.CancelDetail> paymentCancelResponse,
                                  final long paymentId) {

        final PaymentCancelResponse.CancelDetail latestCancelPayment = getLatestCancelPayment(paymentCancelResponse);

        paymentCancelSaver.savePaymentCancel(
                paymentId,
                latestCancelPayment.cancelAmount(),
                latestCancelPayment.transactionKey(),
                parseTossDateToLocalDateTime(latestCancelPayment.canceledAt())
        );
    }

    private PaymentCancelResponse.CancelDetail getLatestCancelPayment(final List<PaymentCancelResponse.CancelDetail> paymentCancelResponse) {
        return  DateFormatterUtil.getLatestCancelPaymentByDate(paymentCancelResponse).orElseThrow(
                () -> new DateFormatException(ErrorCode.INTERNAL_ISO_DATE_ERROR)
        );
    }

    private void updateTicketStatus(final List<TicketEntity> ticketEntity, final TicketStatus ticketStatus) {
        ticketEntity.forEach(
                ticket -> ticketUpdater.updateTicketStatus(ticket, ticketStatus)
        );
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
                parseTossDateToLocalDateTime(tossPaymentResponse.requestedAt()),
                parseTossDateToLocalDateTime(tossPaymentResponse.approvedAt())
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

    private void updateCouponToUsedTrue(final String couponCode) {
        final CouponEntity couponEntity = couponRetriever.findCouponEntityByCouponCode(couponCode);
        couponUpdater.updateCouponUsed(couponEntity, true);
    }
}
