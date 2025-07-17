package com.permitseoul.permitserver.domain.payment.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.payment.api.client.TossPaymentClient;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentCancelResponse;
import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentResponse;
import com.permitseoul.permitserver.domain.payment.api.dto.TossConfirmErrorResponse;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentRetriever;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentSaver;
import com.permitseoul.permitserver.domain.payment.core.domain.Currency;
import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import com.permitseoul.permitserver.domain.payment.core.exception.PaymentNotFoundException;
import com.permitseoul.permitserver.domain.paymentcancel.core.component.PaymentCancelSaver;
import com.permitseoul.permitserver.domain.reservation.api.TossProperties;
import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentCancelRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentConfirmResponse;
import com.permitseoul.permitserver.domain.reservation.api.exception.*;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationUpdater;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketRetriever;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketRetriever;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketUpdater;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import com.permitseoul.permitserver.domain.ticket.core.exception.TicketNotFoundException;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeUpdater;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeInsufficientCountException;
import com.permitseoul.permitserver.global.TicketCodeGenerator;
import com.permitseoul.permitserver.global.exception.AlgorithmException;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import com.permitseoul.permitserver.global.exception.IllegalEnumTransitionException;
import com.permitseoul.permitserver.global.formatter.DateFormatterUtil;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import feign.FeignException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

import static com.permitseoul.permitserver.global.formatter.DateFormatterUtil.parseDateToLocalDateTime;

@Service
@EnableConfigurationProperties(TossProperties.class)
public class PaymentService {
    private static final String COLON = ":";
    private static final String AUTH_TYPE_BASIC = "Basic ";
    private static final String JSON_PARSING_FAIL = "JSON 파싱 실패입니다.";
    private static final String PAYMENT_FEIGN_FAIL = "결제 Feign 통신 오류입니다.";
    private static final String CANCEL_REASON = "고객 요구 취소";

    private final ReservationTicketRetriever reservationTicketRetriever;
    private final EventRetriever eventRetriever;
    private final TossPaymentClient tossPaymentClient;
    private final ReservationRetriever reservationRetriever;
    private final TossProperties tossProperties;
    private final String authorizationHeader;
    private final PaymentSaver paymentSaver;
    private final TicketTypeRetriever ticketTypeRetriever;
    private final PaymentRetriever paymentRetriever;
    private final PaymentCancelSaver paymentCancelSaver;
    private final TicketUpdater ticketUpdater;
    private final TicketRetriever ticketRetriever;
    private final ReservationUpdater reservationUpdater;
    private final TicketReservationFacade ticketReservationFacade;
    private final TicketTypeUpdater ticketTypeUpdater;

    public PaymentService(
            ReservationTicketRetriever reservationTicketRetriever,
            EventRetriever eventRetriever,
            TossPaymentClient tossPaymentClient,
            ReservationRetriever reservationRetriever,
            TossProperties tossProperties,
            PaymentSaver paymentSaver,
            TicketTypeRetriever ticketTypeRetriever,
            PaymentRetriever paymentRetriever,
            PaymentCancelSaver paymentCancelSaver,
            TicketUpdater ticketUpdater,
            TicketRetriever ticketRetriever,
            ReservationUpdater reservationUpdater,
            TicketReservationFacade ticketReservationFacade,
            TicketTypeUpdater ticketTypeUpdater) {
        this.reservationTicketRetriever = reservationTicketRetriever;
        this.eventRetriever = eventRetriever;
        this.tossPaymentClient = tossPaymentClient;
        this.reservationRetriever = reservationRetriever;
        this.tossProperties = tossProperties;
        this.authorizationHeader = buildAuthHeader(tossProperties.apiSecretKey());
        this.paymentSaver = paymentSaver;
        this.ticketTypeRetriever = ticketTypeRetriever;
        this.paymentRetriever = paymentRetriever;
        this.paymentCancelSaver = paymentCancelSaver;
        this.ticketUpdater = ticketUpdater;
        this.ticketRetriever = ticketRetriever;
        this.reservationUpdater = reservationUpdater;
        this.ticketReservationFacade = ticketReservationFacade;
        this.ticketTypeUpdater = ticketTypeUpdater;
    }


    public PaymentConfirmResponse getPaymentConfirm(final long userId,
                                                    final String orderId,
                                                    final String paymentKey,
                                                    final BigDecimal totalAmount) {
        ReservationEntity reservationEntity = null;
        try {
            reservationEntity = reservationRetriever.findReservationByOrderIdAndAmountAndUserId(orderId, totalAmount, userId);
            final Event event = eventRetriever.findEventById(reservationEntity.getEventId());

            final List<ReservationTicket> findReservationTicketList = reservationTicketRetriever.findAllByOrderId(orderId);
            verifyTicketCount(findReservationTicketList);

            final PaymentResponse paymentResponse = getTossPaymentConfirm(authorizationHeader, paymentKey, reservationEntity.getOrderId(), reservationEntity.getTotalAmount());
            updateReservationStatus(reservationEntity, ReservationStatus.PAYMENT_SUCCESS);
            savePaymentInfo(reservationEntity, paymentResponse);

            final List<Ticket> newTicketList = generateTickets(findReservationTicketList, userId, reservationEntity);
            ticketReservationFacade.saveAllTickets(newTicketList, reservationEntity, findReservationTicketList);

            return PaymentConfirmResponse.of(
                    event.getName(),
                    DateFormatterUtil.formatEventDate(event.getStartDate(), event.getEndDate())
            );
        } catch (ReservationNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_RESERVATION);
        } catch (EventNotfoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_EVENT);
        } catch (TicketTypeNotfoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        } catch(FeignException e) {
            if (reservationEntity != null) {
                updateReservationStatus(reservationEntity, ReservationStatus.PAYMENT_FAILED);
            }
            throw handleFeignException(e);
        } catch (AlgorithmException e) {
            throw new TicketAlgorithmException(ErrorCode.INTERNAL_TICKET_ALGORITHM_ERROR);
        } catch (TicketTypeInsufficientCountException e) {
            throw new ConflictReservationException(ErrorCode.CONFLICT_INSUFFICIENT_TICKET);
        } catch (IllegalEnumTransitionException e) {
            throw new ReservationIllegalException(ErrorCode.INTERNAL_TRANSITION_ENUM_ERROR);
        }
    }

    public void cancelPayment(final long userId, final String orderId) {
        try {
            final PaymentEntity paymentEntity = paymentRetriever.findPaymentEntityByOrderId(orderId);
            final List<TicketEntity> ticketEntity = ticketRetriever.findAllTicketsByOrderIdAndUserId(paymentEntity.getOrderId(), userId);
            final ReservationEntity reservationEntity = reservationRetriever.findReservationEntityByIdAndUserId(paymentEntity.getReservationId(), userId);
            final List<ReservationTicket> reservationTicketList = reservationTicketRetriever.findAllByOrderId(orderId);


            final PaymentCancelResponse paymentCancelResponse = cancelTossPayment(
                    paymentEntity.getPaymentId(),
                    paymentEntity.getPaymentKey(),
                    paymentEntity.getCurrency()
            );


            updateTicketStatus(ticketEntity, TicketStatus.CANCELED);
            updateReservationStatus(reservationEntity, ReservationStatus.PAYMENT_CANCELED);
            increaseRedisTicketTypeCount(reservationTicketList);

            final PaymentCancelResponse.CancelDetail latestCancelPayment = DateFormatterUtil.getLatestCancelPaymentByDate(paymentCancelResponse.cancels()).orElseThrow(
                    () -> new DateFormatException(ErrorCode.INTERNAL_ISO_DATE_ERROR)
            );

            paymentCancelSaver.savePaymentCancel(
                    paymentEntity.getPaymentId(),
                    latestCancelPayment.cancelAmount(),
                    latestCancelPayment.transactionKey(),
                    parseDateToLocalDateTime(latestCancelPayment.canceledAt())
            );
        } catch (PaymentNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_PAYMENT);
        } catch (ReservationNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_RESERVATION);
        } catch (TicketNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_TICKET);
        }
    }

    private void increaseRedisTicketTypeCount(final List<ReservationTicket> reservationTicketList) {
        reservationTicketList.forEach(
                reservationTicket -> {
                    final TicketTypeEntity ticketTypeEntity = ticketTypeRetriever.findTicketTypeEntityById(reservationTicket.getTicketTypeId());
                    ticketTypeUpdater.increaseTicketCount(ticketTypeEntity, reservationTicket.getCount());
                }
        );
    }

    private void updateReservationStatus(final ReservationEntity reservationEntity, final ReservationStatus reservationStatus) {
        reservationUpdater.updateReservationStatus(reservationEntity, reservationStatus);
    }

    private void updateTicketStatus(final List<TicketEntity> ticketEntity, final TicketStatus ticketStatus) {
        ticketEntity.forEach(
                ticket -> ticketUpdater.updateTicketStatus(ticket, ticketStatus)
        );
    }

    private PaymentCancelResponse cancelTossPayment(final long paymentId, final String paymentKey, final Currency currency) {
        return tossPaymentClient.cancelPayment(
                authorizationHeader,
                paymentKey,
                TossPaymentCancelRequest.of(CANCEL_REASON, currency)
        );
    }

    private String buildAuthHeader(final String secretKey) {
        return AUTH_TYPE_BASIC + Base64.getEncoder().encodeToString((secretKey + COLON).getBytes());
    }

    private PaymentResponse getTossPaymentConfirm(final String authorizationHeader,
                                                  final String paymentKey,
                                                  final String orderId,
                                                  final BigDecimal totalAmount) {
        return  tossPaymentClient.purchaseConfirm(
                authorizationHeader,
                TossPaymentRequest.of(paymentKey, orderId, totalAmount)
        );
    }

    private void verifyTicketCount(final List<ReservationTicket> reservationTicketList) {
        reservationTicketList.forEach(
                reservationTicket -> {
                    final TicketTypeEntity ticketTypeEntity = ticketTypeRetriever.findTicketTypeEntityById(reservationTicket.getTicketTypeId());
                    ticketTypeRetriever.verifyTicketCount(ticketTypeEntity, reservationTicket.getCount());
                });
    }

    private void savePaymentInfo(final ReservationEntity reservation, final PaymentResponse paymentResponse) {
        paymentSaver.savePayment(
                reservation.getReservationId(),
                reservation.getOrderId(),
                reservation.getEventId(),
                paymentResponse.paymentKey(),
                reservation.getTotalAmount(),
                paymentResponse.currency(),
                parseDateToLocalDateTime(paymentResponse.requestedAt()),
                parseDateToLocalDateTime(paymentResponse.approvedAt())
        );
    }

    private List<Ticket> generateTickets(final List<ReservationTicket> reservationTicketList, final long userId, final ReservationEntity reservation) {
        return reservationTicketList.stream()
                .flatMap(reservationTicket ->
                        IntStream.range(0, reservationTicket.getCount())
                                .mapToObj(i -> Ticket.builder()
                                        .userId(userId)
                                        .orderId(reservationTicket.getOrderId())
                                        .ticketTypeId(reservationTicket.getTicketTypeId())
                                        .eventId(reservation.getEventId())
                                        .ticketCode(TicketCodeGenerator.generateTicketCode())
                                        .status(TicketStatus.RESERVED)
                                        .build()
                                )
                )
                .toList();
    }

    private RuntimeException handleFeignException(final FeignException e) {
        final String body = e.contentUTF8();
        if (body != null && !body.isBlank()) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                final TossConfirmErrorResponse tossError = mapper.readValue(body, TossConfirmErrorResponse.class);
                return new TossPaymentConfirmException(ErrorCode.INTERNAL_PAYMENT_FEIGN_ERROR, tossError.getMessage());
            } catch (JsonProcessingException jsonException) {
                return new PaymentFeignException(
                        ErrorCode.INTERNAL_JSON_FORMAT_ERROR,
                        e.getCause() != null ? e.getCause().getMessage() : JSON_PARSING_FAIL
                );
            }
        } else {
            return new PaymentFeignException(
                    ErrorCode.INTERNAL_PAYMENT_FEIGN_ERROR,
                    e.getCause() != null ? e.getCause().getMessage() : PAYMENT_FEIGN_FAIL
            );
        }
    }
}
