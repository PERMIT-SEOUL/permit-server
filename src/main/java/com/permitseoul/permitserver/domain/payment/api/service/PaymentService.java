package com.permitseoul.permitserver.domain.payment.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.payment.api.client.TossPaymentClient;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentCancelResponse;
import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentResponse;
import com.permitseoul.permitserver.domain.payment.api.dto.TossConfirmErrorResponse;
import com.permitseoul.permitserver.domain.payment.api.exception.NotFoundPaymentException;
import com.permitseoul.permitserver.domain.payment.api.exception.PaymentBadRequestException;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentRetriever;
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
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionRetriever;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession;
import com.permitseoul.permitserver.domain.reservationsession.core.exception.ReservationSessionBadRequestException;
import com.permitseoul.permitserver.domain.reservationsession.core.exception.ReservationSessionNotFoundAfterPaymentSuccessException;
import com.permitseoul.permitserver.domain.reservationsession.core.exception.ReservationSessionNotFoundException;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketRetriever;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.reservationticket.core.exception.ReservationTicketNotFoundException;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketGenerator;
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
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeTicketZeroException;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.exception.AlgorithmException;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import com.permitseoul.permitserver.global.exception.IllegalEnumTransitionException;
import com.permitseoul.permitserver.global.formatter.DateFormatterUtil;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import static com.permitseoul.permitserver.domain.ticket.core.component.TicketGenerator.generatePublicTickets;
import static com.permitseoul.permitserver.global.formatter.DateFormatterUtil.parseDateToLocalDateTime;

@Slf4j
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
    private final TicketTypeRetriever ticketTypeRetriever;
    private final PaymentRetriever paymentRetriever;
    private final PaymentCancelSaver paymentCancelSaver;
    private final TicketUpdater ticketUpdater;
    private final TicketRetriever ticketRetriever;
    private final ReservationUpdater reservationUpdater;
    private final TicketReservationPaymentFacade ticketReservationPaymentFacade;
    private final TicketTypeUpdater ticketTypeUpdater;
    private final ReservationSessionRetriever reservationSessionRetriever;
    private final RedisTemplate<String, String> redisTemplate;


    public PaymentService(
            ReservationTicketRetriever reservationTicketRetriever,
            EventRetriever eventRetriever,
            TossPaymentClient tossPaymentClient,
            ReservationRetriever reservationRetriever,
            TossProperties tossProperties,
            TicketTypeRetriever ticketTypeRetriever,
            PaymentRetriever paymentRetriever,
            PaymentCancelSaver paymentCancelSaver,
            TicketUpdater ticketUpdater,
            TicketRetriever ticketRetriever,
            ReservationUpdater reservationUpdater,
            TicketReservationPaymentFacade ticketReservationPaymentFacade,
            TicketTypeUpdater ticketTypeUpdater,
            ReservationSessionRetriever reservationSessionRetriever,
            RedisTemplate<String, String> redisTemplate) {
        this.reservationTicketRetriever = reservationTicketRetriever;
        this.eventRetriever = eventRetriever;
        this.tossPaymentClient = tossPaymentClient;
        this.reservationRetriever = reservationRetriever;
        this.tossProperties = tossProperties;
        this.authorizationHeader = buildAuthHeader(tossProperties.apiSecretKey());
        this.ticketTypeRetriever = ticketTypeRetriever;
        this.paymentRetriever = paymentRetriever;
        this.paymentCancelSaver = paymentCancelSaver;
        this.ticketUpdater = ticketUpdater;
        this.ticketRetriever = ticketRetriever;
        this.reservationUpdater = reservationUpdater;
        this.ticketReservationPaymentFacade = ticketReservationPaymentFacade;
        this.ticketTypeUpdater = ticketTypeUpdater;
        this.reservationSessionRetriever = reservationSessionRetriever;
        this.redisTemplate = redisTemplate;
    }


    public PaymentConfirmResponse getPaymentConfirm(final long userId,
                                                    final String orderId,
                                                    final String paymentKey,
                                                    final BigDecimal totalAmount,
                                                    final String reservationSessionKey) {
        Reservation reservation = null;
        List<ReservationTicket> reservationTicketList = null;
        try {
            final ReservationSession reservationSession = getValidReservationSession(userId, reservationSessionKey, orderId);
            reservationTicketList = reservationTicketRetriever.findAllByOrderId(reservationSession.getOrderId());
            reservation = reservationRetriever.findReservationByOrderIdAndAmountAndUserId(reservationSession.getOrderId(), totalAmount, userId);
            final Event event = eventRetriever.findEventById(reservation.getEventId());
            verifyTicketCount(reservationTicketList);

            final TossPaymentResponse tossPaymentResponse = getTossPaymentConfirm(authorizationHeader, paymentKey, reservation.getOrderId(), reservation.getTotalAmount());
            updateReservationStatusAndTossPaymentResponseTime(reservation.getReservationId(), ReservationStatus.PAYMENT_SUCCESS);

            final List<Ticket> newTicketList = TicketGenerator.generatePublicTickets(reservationTicketList, userId, reservation);
            ticketReservationPaymentFacade.savePaymentAndAllTickets(
                    newTicketList,
                    reservationTicketList,
                    reservationSession.getReservationSessionsId(),
                    reservation.getReservationId(),
                    tossPaymentResponse
            );

            return PaymentConfirmResponse.of(
                    event.getName(),
                    DateFormatterUtil.formatEventDate(event.getStartDate(), event.getEndDate())
            );
        } catch (ReservationSessionBadRequestException e) {
            logRollbackFailed(userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new PaymentBadRequestException(ErrorCode.BAD_REQUEST_SESSION_ORDER_ID);

        } catch (ReservationSessionNotFoundException e) {
            logRollbackFailed(userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_RESERVATION_SESSION);

        } catch (ReservationTicketNotFoundException e ){
            logRollbackFailed(userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_RESERVATION_TICKET);

        } catch (ReservationNotFoundException e) {
            sessionRedisRollback(reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_RESERVATION);

        } catch (EventNotfoundException e) {
            sessionRedisRollback(reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_EVENT);

        }  catch (TicketTypeNotfoundException e) {
            sessionRedisRollback(reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_TICKET_TYPE);

        } catch (TicketTypeInsufficientCountException e) {
            sessionRedisRollback(reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new ConflictReservationException(ErrorCode.CONFLICT_INSUFFICIENT_TICKET);

        } catch (TicketTypeTicketZeroException e) {
            sessionRedisRollback(reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new PaymentBadRequestException(ErrorCode.BAD_REQUEST_TICKET_COUNT_ZERO);

        } catch(FeignException e) {
            handleFailedTossPayment(reservation, reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw handleFeignException(e, orderId, userId);

        } catch (AlgorithmException e) { //결제는 됐는데, 티켓 발급 과정에서 실패했으므로, 따로 알림 구축해놔야될듯
            logPaymentSuccessButTicketIssueFailed(userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new TicketAlgorithmException(ErrorCode.INTERNAL_TICKET_ALGORITHM_ERROR);

        } catch (IllegalEnumTransitionException e) { //결제는 됐는데, 티켓 발급 과정에서 실패했으므로, 따로 알림 구축해놔야될듯
            logPaymentSuccessButTicketIssueFailed(userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new ReservationIllegalException(ErrorCode.INTERNAL_TRANSITION_ENUM_ERROR);
        } catch(ReservationSessionNotFoundAfterPaymentSuccessException e) {  //결제는 됐는데, 티켓 발급 과정에서 실패했으므로, 따로 알림 구축해놔야될듯
            logPaymentSuccessButTicketIssueFailed(userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_RESERVATION_SESSION_AFTER_PAYMENT_SUCCESS);
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

    private void handleFailedTossPayment(final Reservation reservation,
                                         final List<ReservationTicket> reservationTicketList,
                                         final long userId,
                                         final String sessionKey,
                                         final String orderId,
                                         final BigDecimal totalAmount,
                                         final String paymentKey
                                         ) {
        if (reservation != null && reservationTicketList != null) {
            updateReservationStatusAndTossPaymentResponseTime(reservation.getReservationId(), ReservationStatus.PAYMENT_FAILED);
            reservationSessionRedisRollback(reservationTicketList, userId, orderId);
        } else {
            logRollbackFailed(userId, sessionKey, orderId, totalAmount, paymentKey);
        }
        log.error("[결제 승인 API - 토스 페이먼츠 결제 실패 ]userId: {}, sessionKey: {}, orderId: {}", userId, sessionKey, orderId);
    }

    private void logPaymentSuccessButTicketIssueFailed( final long userId,
                                                        final String sessionKey,
                                                        final String orderId,
                                                        final BigDecimal totalAmount,
                                                        final String paymentKey) {
        log.error("[결제 승인 API - 토스 페이먼츠 결제 성공 후 티켓 발급 실패] userId: {}, sessionKey: {}, orderId: {}, totalAmount: {}, paymentKey: {}",
                userId, sessionKey, orderId, totalAmount, paymentKey);
    }

    private void sessionRedisRollback(final List<ReservationTicket> reservationTicketList,
                                      final long userId,
                                      final String sessionKey,
                                      final String orderId,
                                      final BigDecimal totalAmount,
                                      final String paymentKey
    ) {
        if (reservationTicketList != null) {
            reservationSessionRedisRollback(reservationTicketList, userId, orderId);
        } else {
            logRollbackFailed(userId, sessionKey, orderId, totalAmount, paymentKey);
        }
    }

    private void logRollbackFailed(final long userId,
                                   final String sessionKey,
                                   final String orderId,
                                   final BigDecimal totalAmount,
                                   final String paymentKey) {
        log.warn("[결제 승인 API - redis Rollback Failed] userId: {}, sessionKey: {}, orderId: {}, totalAmount: {}, paymentKey: {}",
                userId, sessionKey, orderId, totalAmount, paymentKey);
    }

    private void updateReservationStatusAndTossPaymentResponseTime(final long reservationId, final ReservationStatus status) {
        ticketReservationPaymentFacade.updateReservationStatusAndTossResponseTime(reservationId, status);
    }



    private void increaseRedisTicketTypeCount(final List<ReservationTicket> reservationTicketList) {
        reservationTicketList.forEach(
                reservationTicket -> {
                    final TicketTypeEntity ticketTypeEntity = ticketTypeRetriever.findTicketTypeEntityById(reservationTicket.getTicketTypeId());
                    ticketTypeUpdater.increaseTicketCount(ticketTypeEntity, reservationTicket.getCount());
                }
        );
    }

    private ReservationSession getValidReservationSession(final long userId, final String sessionKey, final String orderId) {
        final LocalDateTime validTime = LocalDateTime.now().minusMinutes(7);
        final ReservationSession reservationSession = reservationSessionRetriever.getValidatedReservationSession(userId, sessionKey, validTime);
        if (!reservationSession.getOrderId().equals(orderId)) {
            throw new ReservationSessionBadRequestException();
        }
        return reservationSession;
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

    private TossPaymentResponse getTossPaymentConfirm(final String authorizationHeader,
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

    private RuntimeException handleFeignException(final FeignException e, final String orderId, final long userId) {
        final String body = e.contentUTF8();
        if (body != null && !body.isBlank()) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                final TossConfirmErrorResponse tossError = mapper.readValue(body, TossConfirmErrorResponse.class);
                log.error("[FEIGN ERROR - 토스 결제 승인 에러] userId={}, orderId={}, 에러메세지={}", userId, orderId, tossError.getMessage());
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

    private void reservationSessionRedisRollback(final List<ReservationTicket> findReservationTicketList,
                                                 final long userId,
                                                 final String orderId) {
        findReservationTicketList.forEach(
                reservationTicket -> {
                    final String redisKey = Constants.REDIS_TICKET_TYPE_KEY_NAME + reservationTicket.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
                    redisTemplate.opsForValue().increment(redisKey, reservationTicket.getCount());
                    log.info("[Redis Increment Rollback 성공] userId={}, orderId={}, ticketTypeId={}. count={}",
                            userId, orderId, reservationTicket.getTicketTypeId(), reservationTicket.getCount());
                }
        );
    }
}
