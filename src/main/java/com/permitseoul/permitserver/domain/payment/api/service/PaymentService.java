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
import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.payment.core.exception.PaymentNotFoundException;
import com.permitseoul.permitserver.domain.reservation.api.TossProperties;
import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentCancelRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentConfirmResponse;
import com.permitseoul.permitserver.domain.reservation.api.exception.*;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionRemover;
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
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.exception.TicketNotFoundException;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeInsufficientCountException;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeTicketZeroException;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.exception.AlgorithmException;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import com.permitseoul.permitserver.global.exception.IllegalEnumTransitionException;
import com.permitseoul.permitserver.global.exception.RedisUnavailableException;
import com.permitseoul.permitserver.global.redis.RedisManager;
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.LogFormUtil;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;

import static com.permitseoul.permitserver.global.util.LogFormUtil.maskPaymentKey;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Slf4j
@Service
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
    private final String authorizationHeader;
    private final TicketTypeRetriever ticketTypeRetriever;
    private final PaymentRetriever paymentRetriever;
    private final TicketRetriever ticketRetriever;
    private final TicketReservationPaymentFacade ticketReservationPaymentFacade;
    private final ReservationSessionRetriever reservationSessionRetriever;
    private final RedisManager redisManager;
    private final ReservationSessionRemover reservationSessionRemover;


    public PaymentService(
            ReservationTicketRetriever reservationTicketRetriever,
            EventRetriever eventRetriever,
            TossPaymentClient tossPaymentClient,
            ReservationRetriever reservationRetriever,
            TossProperties tossProperties,
            TicketTypeRetriever ticketTypeRetriever,
            PaymentRetriever paymentRetriever,
            TicketRetriever ticketRetriever,
            TicketReservationPaymentFacade ticketReservationPaymentFacade,
            ReservationSessionRetriever reservationSessionRetriever,
            RedisManager redisManager, ReservationSessionRemover reservationSessionRemover) {
        this.reservationTicketRetriever = reservationTicketRetriever;
        this.eventRetriever = eventRetriever;
        this.tossPaymentClient = tossPaymentClient;
        this.reservationRetriever = reservationRetriever;
        this.authorizationHeader = buildAuthHeader(tossProperties.apiSecretKey());
        this.ticketTypeRetriever = ticketTypeRetriever;
        this.paymentRetriever = paymentRetriever;
        this.ticketRetriever = ticketRetriever;
        this.ticketReservationPaymentFacade = ticketReservationPaymentFacade;
        this.reservationSessionRetriever = reservationSessionRetriever;
        this.redisManager = redisManager;
        this.reservationSessionRemover = reservationSessionRemover;
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

            log.info("[Payment] 토스 결제 승인 완료 - orderId={}, paymentKey={}, reservationId={}, amount={}",
                    tossPaymentResponse.orderId(),
                    LogFormUtil.maskPaymentKey(tossPaymentResponse.paymentKey()),
                    reservation.getReservationId(),
                    tossPaymentResponse.totalAmount()
            );

            updateReservationStatusAndTossPaymentResponseTime(reservation.getReservationId(), ReservationStatus.PAYMENT_SUCCESS);

            final List<Long> ticketTypeIds = reservationTicketList.stream()
                    .map(ReservationTicket::getTicketTypeId)
                    .toList();
            final List<TicketTypeEntity> ticketTypeEntities = ticketTypeRetriever.findAllByIds(ticketTypeIds);
            final List<Ticket> newTicketList = TicketGenerator.generatePublicTickets(reservationTicketList, userId, reservation, ticketTypeEntities);

            ticketReservationPaymentFacade.savePaymentAndAllTickets(
                    newTicketList,
                    reservationTicketList,
                    reservationSession.getReservationSessionsId(),
                    reservation.getReservationId(),
                    tossPaymentResponse
            );

            return PaymentConfirmResponse.of(
                    event.getName(),
                    LocalDateTimeFormatterUtil.formatStartEndDate(event.getStartAt(), event.getEndAt())
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
            deleteReservationSessionByOrderId(orderId);
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_RESERVATION);

        } catch (EventNotfoundException e) {
            sessionRedisRollback(reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            deleteReservationSessionByOrderId(orderId);
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_EVENT);

        }  catch (TicketTypeNotfoundException e) {
            sessionRedisRollback(reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            deleteReservationSessionByOrderId(orderId);
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_TICKET_TYPE);

        } catch (TicketTypeInsufficientCountException e) {
            sessionRedisRollback(reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            deleteReservationSessionByOrderId(orderId);
            throw new ConflictReservationException(ErrorCode.CONFLICT_INSUFFICIENT_TICKET);

        } catch (TicketTypeTicketZeroException e) {
            sessionRedisRollback(reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            deleteReservationSessionByOrderId(orderId);
            throw new PaymentBadRequestException(ErrorCode.BAD_REQUEST_TICKET_COUNT_ZERO);

        } catch(FeignException e) {
            handleFailedTossPayment(reservation, reservationTicketList, userId, reservationSessionKey, orderId, totalAmount, paymentKey);
            deleteReservationSessionByOrderId(orderId);
            throw handleFeignException(e, orderId, userId);

        } catch (AlgorithmException e) {
            logPaymentSuccessButTicketIssueFailed(orderId, totalAmount, paymentKey, reservation.getReservationId());
            throw new TicketAlgorithmException(ErrorCode.INTERNAL_TICKET_ALGORITHM_ERROR);

        } catch (IllegalEnumTransitionException e) {
            logPaymentSuccessButTicketIssueFailed(orderId, totalAmount, paymentKey, reservation.getReservationId());
            throw new ReservationIllegalException(ErrorCode.INTERNAL_TRANSITION_ENUM_ERROR);

        } catch (ReservationSessionNotFoundAfterPaymentSuccessException e) {
            logPaymentSuccessButTicketIssueFailed(orderId, totalAmount, paymentKey, reservation.getReservationId());
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_RESERVATION_SESSION_AFTER_PAYMENT_SUCCESS);
        }
    }

    public void cancelPayment(final long userId, final String orderId) {
        try {
            final Payment payment = paymentRetriever.findPaymentByOrderId(orderId);
            validateCancelAvailablePeriod(payment.getEventId());

            final List<Ticket> ticketList = ticketRetriever.findAllTicketsByOrderIdAndUserId(payment.getOrderId(), userId);
            validateTicketStatusForCancel(ticketList);

            final Reservation reservation = reservationRetriever.findReservationByIdAndUserId(payment.getReservationId(), userId);
            final List<ReservationTicket> reservationTicketList = reservationTicketRetriever.findAllByOrderId(orderId);

            final PaymentCancelResponse paymentCancelResponse = cancelTossPayment(
                    payment.getPaymentKey(),
                    payment.getCurrency()
            );

            final List<Long> ticketEntityIdList = ticketList.stream().map(Ticket::getTicketId).toList();

            ticketReservationPaymentFacade.updateTicketAndReservationStatus(
                    ticketEntityIdList,
                    reservation.getReservationId(),
                    TicketStatus.CANCELED,
                    ReservationStatus.PAYMENT_CANCELED
            );

            ticketReservationPaymentFacade.increaseTicketTypeRemainCount(orderId);
            ticketReservationPaymentFacade.saveCancelPayment(paymentCancelResponse.cancels(), payment.getPaymentId());
            reservationSessionRedisRollback(reservationTicketList, userId, payment.getOrderId());

        } catch (PaymentNotFoundException e) {
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_PAYMENT);
        } catch (EventNotfoundException e) {
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_EVENT);
        } catch(FeignException e) {
            throw handleFeignException(e, orderId, userId);
        } catch (TicketNotFoundException e) {
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_TICKET);
        } catch (ReservationNotFoundException e) {
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_RESERVATION);
        } catch (ReservationTicketNotFoundException e) {
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_RESERVATION_TICKET);
        } catch (TicketTypeNotfoundException e) {
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        } catch (DataIntegrityViolationException e) {
            log.error("[PAYMENT CANCEL] DB는 재고 복구 완료, redis 복구 실패 userID={}, orderId={}", userId, orderId, e.getCause());
            throw e;
        } catch(DateFormatException e) {
            throw new PaymentBadRequestException(ErrorCode.INTERNAL_ISO_DATE_ERROR);
        }
    }

    private void validateCancelAvailablePeriod(final long eventId) {
        final Event event = eventRetriever.findEventById(eventId);

        final LocalDate eventDate = event.getStartAt().toLocalDate();
        final LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 오늘과 행사일 사이의 일수 계산
        long daysUntilEvent = ChronoUnit.DAYS.between(today, eventDate);

        // 3일 전까지만 환불 가능
        if (daysUntilEvent < 3) {
            log.warn("[Payment Cancel] 취소 기한 초과 - eventId={}, eventDate={}, today={}, daysUntilEvent={}",
                    eventId, eventDate, today, daysUntilEvent);
            throw new PaymentBadRequestException(ErrorCode.BAD_REQUEST_CANCEL_PERIOD_EXPIRED);
        }
    }

    private void deleteReservationSessionByOrderId(final String orderId) {
        try {
            reservationSessionRemover.deleteByOrderId(orderId);
        } catch (Exception e) {
            log.error("[Payment] 결제 실패 후, Reservation Session 삭제 실패(중복 redis rollback 가능성) orderId={}", orderId, e);
        }

    }

    private void validateTicketStatusForCancel(final List<Ticket> ticketList) {
        for (final Ticket ticket : ticketList) {
            switch (ticket.getStatus()) {
                case USED -> throw new PaymentBadRequestException(ErrorCode.CONFLICT_ALREADY_USED_TICKET_CANCEL);
                case CANCELED -> throw new PaymentBadRequestException(ErrorCode.CONFLICT_ALREADY_CANCELED_TICKET_CANCEL);
            }
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
        log.error("[결제 승인 API - 토스 페이먼츠 결제 실패] userId: {}, sessionKey: {}, orderId: {}", userId, sessionKey, orderId);
        if (reservation != null && reservationTicketList != null) {
            updateReservationStatusAndTossPaymentResponseTime(reservation.getReservationId(), ReservationStatus.PAYMENT_FAILED);
            try {
                reservationSessionRedisRollback(reservationTicketList, userId, orderId);
            } catch (RedisUnavailableException e) {
                log.error("[결제 승인 API - redis Rollback Failed] userId: {}, sessionKey: {}, orderId: {}", userId, sessionKey, orderId, e);
            }
        } else {
            logRollbackFailed(userId, sessionKey, orderId, totalAmount, paymentKey);
        }
    }

    private void logPaymentSuccessButTicketIssueFailed(final String orderId,
                                                       final BigDecimal totalAmount,
                                                       final String paymentKey,
                                                       final long reservationId) {
        log.error("[Payment] 토스 결제 승인 완료 -> 티켓 발급 실패 - orderId={}, paymentKey={}, reservationId={}, amount={}",
                orderId,
                LogFormUtil.maskPaymentKey(paymentKey),
                reservationId,
                totalAmount
        );
    }

    private void sessionRedisRollback(final List<ReservationTicket> reservationTicketList,
                                      final long userId,
                                      final String sessionKey,
                                      final String orderId,
                                      final BigDecimal totalAmount,
                                      final String paymentKey
    ) {
        if (reservationTicketList != null) {
            try {
                reservationSessionRedisRollback(reservationTicketList, userId, orderId);
            } catch (RedisUnavailableException e) {
                log.error("[결제 승인 API - redis Rollback Failed] userId: {}, sessionKey: {}, orderId: {}", userId, sessionKey, orderId, e);
            }
        } else {
            logRollbackFailed(userId, sessionKey, orderId, totalAmount, paymentKey);
        }
    }

    private void logRollbackFailed(final long userId,
                                   final String sessionKey,
                                   final String orderId,
                                   final BigDecimal totalAmount,
                                   final String paymentKey) {
        log.error("[결제 승인 API - redis Rollback Failed] userId: {}, sessionKey: {}, orderId: {}, totalAmount: {}, paymentKey: {}",
                userId, sessionKey, orderId, totalAmount, maskPaymentKey(paymentKey));
    }

    private void updateReservationStatusAndTossPaymentResponseTime(final long reservationId, final ReservationStatus status) {
        ticketReservationPaymentFacade.updateReservationStatusAndTossResponseTime(reservationId, status);
    }

    private ReservationSession getValidReservationSession(final long userId, final String sessionKey, final String orderId) {
        final LocalDateTime validTime = LocalDateTime.now().minusMinutes(7);
        final ReservationSession reservationSession = reservationSessionRetriever.getValidatedReservationSession(userId, sessionKey, validTime);
        if (!reservationSession.getOrderId().equals(orderId)) {
            throw new ReservationSessionBadRequestException();
        }
        return reservationSession;
    }

    private PaymentCancelResponse cancelTossPayment(final String paymentKey, final Currency currency) {
        return tossPaymentClient.cancelPayment(
                authorizationHeader,
                paymentKey,
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

        return tossPaymentClient.purchaseConfirm(
                authorizationHeader,
                orderId,
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
                log.error("[FEIGN ERROR - 토스 페이먼츠 에러] userId={}, orderId={}, 에러메세지={}", userId, orderId, tossError.getMessage());
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
                    redisManager.increment(redisKey, reservationTicket.getCount());
                    log.info("[Redis Increment Rollback 성공] userId={}, orderId={}, ticketTypeId={}. count={}",
                            userId, orderId, reservationTicket.getTicketTypeId(), reservationTicket.getCount());
                }
        );
    }
}
