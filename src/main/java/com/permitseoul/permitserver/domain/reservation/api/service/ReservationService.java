package com.permitseoul.permitserver.domain.reservation.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.payment.api.client.TossPaymentClient;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentResponse;
import com.permitseoul.permitserver.domain.payment.api.dto.TossConfirmErrorResponse;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentSaver;
import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.reservation.api.TossProperties;
import com.permitseoul.permitserver.domain.reservation.api.dto.PaymentConfirmResponse;
import com.permitseoul.permitserver.domain.reservation.api.dto.PaymentReadyRequest;
import com.permitseoul.permitserver.domain.reservation.api.dto.PaymentReadyResponse;
import com.permitseoul.permitserver.domain.reservation.api.exception.*;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationSaver;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketRetriever;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketSaver;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketSaver;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeInsufficientCountException;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import com.permitseoul.permitserver.global.TicketCodeGenerator;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.global.exception.AlgorithmException;
import com.permitseoul.permitserver.global.formatter.EventDateFormatterUtil;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import feign.FeignException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

@Service
@EnableConfigurationProperties(TossProperties.class)
public class ReservationService {
    private static final String COLON = ":";
    private static final String AUTH_TYPE_BASIC = "Basic ";

    private final ReservationSaver reservationSaver;
    private final ReservationTicketSaver reservationTicketSaver;
    private final ReservationTicketRetriever reservationTicketRetriever;
    private final EventRetriever eventRetriever;
    private final UserRetriever userRetriever;
    private final TossPaymentClient tossPaymentClient;
    private final ReservationRetriever reservationRetriever;
    private final TossProperties tossProperties;
    private final String authorizationHeader;
    private final PaymentSaver paymentSaver;
    private final TicketSaver ticketSaver;
    private final TicketTypeRetriever ticketTypeRetriever;


    public ReservationService(ReservationSaver reservationSaver,
                              ReservationTicketSaver reservationTicketSaver,
                              ReservationTicketRetriever reservationTicketRetriever,
                              EventRetriever eventRetriever,
                              UserRetriever userRetriever,
                              TossPaymentClient tossPaymentClient,
                              ReservationRetriever reservationRetriever,
                              TossProperties tossProperties,
                              PaymentSaver paymentSaver,
                              TicketSaver ticketSaver,
                              TicketTypeRetriever ticketTypeRetriever, TicketTypeRepository ticketTypeRepository) {
        this.reservationSaver = reservationSaver;
        this.reservationTicketSaver = reservationTicketSaver;
        this.reservationTicketRetriever = reservationTicketRetriever;
        this.eventRetriever = eventRetriever;
        this.userRetriever = userRetriever;
        this.tossPaymentClient = tossPaymentClient;
        this.reservationRetriever = reservationRetriever;
        this.tossProperties = tossProperties;
        this.authorizationHeader = buildAuthHeader(tossProperties.apiSecretKey());
        this.paymentSaver = paymentSaver;
        this.ticketSaver = ticketSaver;
        this.ticketTypeRetriever = ticketTypeRetriever;
    }

    @Transactional
    public PaymentReadyResponse getPaymentReady(final long userId,
                                                final long eventId,
                                                final String couponCode,
                                                final BigDecimal totalAmount,
                                                final String orderId,
                                                final List<PaymentReadyRequest.TicketTypeInfo> ticketTypeInfos) {
        final Event event;
        final User user;
        try {
            event = eventRetriever.findEventById(eventId);
            user = userRetriever.findUserById(userId);
        } catch (EventNotfoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_EVENT);
        } catch (UserNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_USER);
        }

        //여기서 터지는 dataintegrationException은 글로벌 핸들러에서 잡고있음.
        final Reservation reservation = reservationSaver.saveReservation(userId, eventId, orderId, totalAmount, couponCode, ReservationStatus.PENDING);
        ticketTypeInfos.forEach(
                ticketTypeInfo -> reservationTicketSaver.saveReservationTicket(ticketTypeInfo.id(), reservation.getOrderId(), ticketTypeInfo.count())
        );

        return PaymentReadyResponse.of(event.getName(), reservation.getOrderId(), user.getName(), user.getEmail(), reservation.getTotalAmount(), user.getSocialId());
    }

    @Transactional
    public PaymentConfirmResponse getPaymentConfirm(final long userId,
                                                    final String orderId,
                                                    final String paymentKey,
                                                    final BigDecimal totalAmount) {
        try {
            final Reservation reservation = reservationRetriever.findReservationByOrderIdAndAmount(orderId, totalAmount, userId);
            final Event event = eventRetriever.findEventById(reservation.getEventId());
            final PaymentResponse paymentResponse = tossPaymentClient.purchaseConfirm(
                    authorizationHeader,
                    PaymentRequest.of(paymentKey, reservation.getOrderId(), reservation.getTotalAmount()));

            //결제정보저장
            final Payment savedPayment = paymentSaver.savePayment(
                    reservation.getReservationId(),
                    reservation.getOrderId(),
                    reservation.getEventId(),
                    paymentResponse.paymentKey(),
                    reservation.getTotalAmount(),
                    paymentResponse.currency());
            final List<ReservationTicket> findReservationTicket = reservationTicketRetriever.findAllByOrderId(savedPayment.getOrderId());

            //티켓개수차감
            findReservationTicket.forEach(reservationTicket -> {
                final TicketTypeEntity ticketTypeEntity = ticketTypeRetriever.findTicketTypeEntityById(reservationTicket.getTicketTypeId());
                ticketTypeEntity.decreaseRemainCount(reservationTicket.getCount());
            });

            //티켓생성
            final List<Ticket> newTickets = findReservationTicket.stream()
                    .flatMap(reservationTicket ->
                            IntStream.range(0, reservationTicket.getCount())
                                    .mapToObj(i -> Ticket.builder()
                                            .userId(userId)
                                            .orderId(reservationTicket.getOrderId())
                                            .ticketTypeId(reservationTicket.getTicketTypeId())
                                            .eventId(reservation.getEventId())
                                            .ticketCode(TicketCodeGenerator.generateTicketCode())
                                            .isUsed(false)
                                            .status(TicketStatus.RESERVED)
                                            .build()
                                    )
                    )
                    .toList();
            ticketSaver.saveTickets(newTickets);

            return PaymentConfirmResponse.of(
                    event.getName(),
                    EventDateFormatterUtil.formatEventDate(event.getStartDate(), event.getEndDate())
            );
        } catch (ReservationNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_RESERVATION);
        } catch(EventNotfoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_EVENT);
        } catch (TicketTypeNotfoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        } catch(FeignException e) {
            final String body = e.contentUTF8();
            if (body != null && !body.isBlank()) {
                try {
                    final ObjectMapper mapper = new ObjectMapper();
                    final TossConfirmErrorResponse tossError = mapper.readValue(body, TossConfirmErrorResponse.class);
                    throw new TossPaymentConfirmException(ErrorCode.UNAUTHORIZED, tossError.getMessage());
                } catch (JsonProcessingException jsonException) {
                    throw new PaymentFeignException(ErrorCode.INTERNAL_JSON_FORMAT_ERROR, e.getCause().getMessage());
                }
            } else {
                throw new PaymentFeignException(ErrorCode.INTERNAL_FEIGN_ERROR, e.getCause().getMessage());
            }
        } catch (AlgorithmException e) {
            throw new TicketAlgorithmException(ErrorCode.INTERNAL_TICKET_ALGORITHM_ERROR);
        } catch (TicketTypeInsufficientCountException e) {
            throw new ConflictReservationException(ErrorCode.CONFLICT_INSUFFICIENT_TICKET);
        }
    }

    private String buildAuthHeader(final String secretKey) {
        return AUTH_TYPE_BASIC + Base64.getEncoder().encodeToString((secretKey + COLON).getBytes());
    }
}
