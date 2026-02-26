package com.permitseoul.permitserver.domain.payment.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.payment.api.client.TossPaymentClient;
import com.permitseoul.permitserver.domain.payment.api.exception.NotFoundPaymentException;
import com.permitseoul.permitserver.domain.payment.api.exception.PaymentBadRequestException;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentRetriever;
import com.permitseoul.permitserver.domain.payment.core.domain.Currency;
import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.payment.core.exception.PaymentNotFoundException;
import com.permitseoul.permitserver.domain.reservation.api.TossProperties;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionRemover;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionRetriever;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketRetriever;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketRetriever;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.exception.TicketNotFoundException;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.global.redis.RedisManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 테스트")
class PaymentServiceTest {

    @Mock
    private ReservationTicketRetriever reservationTicketRetriever;
    @Mock
    private EventRetriever eventRetriever;
    @Mock
    private TossPaymentClient tossPaymentClient;
    @Mock
    private ReservationRetriever reservationRetriever;
    @Mock
    private TicketTypeRetriever ticketTypeRetriever;
    @Mock
    private PaymentRetriever paymentRetriever;
    @Mock
    private TicketRetriever ticketRetriever;
    @Mock
    private TicketReservationPaymentFacade ticketReservationPaymentFacade;
    @Mock
    private ReservationSessionRetriever reservationSessionRetriever;
    @Mock
    private RedisManager redisManager;
    @Mock
    private ReservationSessionRemover reservationSessionRemover;

    private PaymentService paymentService;

    // ── 공통 테스트 데이터 ──
    private static final long USER_ID = 1L;
    private static final long EVENT_ID = 100L;
    private static final String ORDER_ID = "ORDER-20260213-001";
    private static final String PAYMENT_KEY = "toss_pk_test_abc123";
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 2, 13, 14, 0);

    @BeforeEach
    void setUp() {
        final TossProperties tossProperties = new TossProperties("test_secret_key");
        paymentService = new PaymentService(
                reservationTicketRetriever,
                eventRetriever,
                tossPaymentClient,
                reservationRetriever,
                tossProperties,
                ticketTypeRetriever,
                paymentRetriever,
                ticketRetriever,
                ticketReservationPaymentFacade,
                reservationSessionRetriever,
                redisManager,
                reservationSessionRemover);
    }

    private Payment createPayment() {
        return new Payment(1L, 1L, ORDER_ID, EVENT_ID, PAYMENT_KEY,
                new BigDecimal("60000"), Currency.KRW, NOW, NOW);
    }

    private Event createEventWithStartAt(final LocalDateTime startAt) {
        return new Event(EVENT_ID, "테스트 이벤트", EventType.PERMIT, startAt, startAt.plusDays(1),
                "서울", "라인업", "상세", 0, NOW.minusDays(30), NOW.plusDays(30), "CHECK-CODE");
    }

    private Ticket createTicket(final TicketStatus status) {
        return Ticket.builder()
                .ticketId(1L)
                .userId(USER_ID)
                .orderId(ORDER_ID)
                .ticketTypeId(10L)
                .eventId(EVENT_ID)
                .ticketCode("TKT-001")
                .status(status)
                .createdAt(NOW)
                .ticketPrice(new BigDecimal("60000"))
                .build();
    }

    private Reservation createReservation() {
        return new Reservation(1L, "[테스트 이벤트] 1일권x1", USER_ID, EVENT_ID, ORDER_ID,
                new BigDecimal("60000"), null, ReservationStatus.PAYMENT_SUCCESS, null);
    }

    // ══════════════════════════════════════════════════════════════
    // cancelPayment
    // ══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("cancelPayment")
    class CancelPaymentTest {

        @Test
        @DisplayName("예외: 결제 내역 미존재")
        void throwsWhenPaymentNotFound() {
            when(paymentRetriever.findPaymentByOrderId(ORDER_ID))
                    .thenThrow(new PaymentNotFoundException());

            assertThatThrownBy(() -> paymentService.cancelPayment(USER_ID, ORDER_ID))
                    .isInstanceOf(NotFoundPaymentException.class);
        }

        @Test
        @DisplayName("예외: 이벤트 미존재")
        void throwsWhenEventNotFound() {
            final Payment payment = createPayment();
            when(paymentRetriever.findPaymentByOrderId(ORDER_ID)).thenReturn(payment);
            when(eventRetriever.findEventById(EVENT_ID)).thenThrow(new EventNotfoundException());

            assertThatThrownBy(() -> paymentService.cancelPayment(USER_ID, ORDER_ID))
                    .isInstanceOf(NotFoundPaymentException.class);
        }

        @Test
        @DisplayName("예외: 취소 기간 초과 (이벤트 3일 이내)")
        void throwsWhenCancelPeriodExpired() {
            final Payment payment = createPayment();
            // 이벤트가 내일이라면 daysUntilEvent=1 → 3 미만 → 취소 불가
            final Event event = createEventWithStartAt(LocalDateTime.now().plusDays(1));
            when(paymentRetriever.findPaymentByOrderId(ORDER_ID)).thenReturn(payment);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);

            assertThatThrownBy(() -> paymentService.cancelPayment(USER_ID, ORDER_ID))
                    .isInstanceOf(PaymentBadRequestException.class);
        }

        @Test
        @DisplayName("예외: 이미 사용된 티켓")
        void throwsWhenTicketUsed() {
            final Payment payment = createPayment();
            final Event event = createEventWithStartAt(LocalDateTime.now().plusDays(30));
            final Ticket usedTicket = createTicket(TicketStatus.USED);

            when(paymentRetriever.findPaymentByOrderId(ORDER_ID)).thenReturn(payment);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(ticketRetriever.findAllTicketsByOrderIdAndUserId(ORDER_ID, USER_ID))
                    .thenReturn(List.of(usedTicket));

            assertThatThrownBy(() -> paymentService.cancelPayment(USER_ID, ORDER_ID))
                    .isInstanceOf(PaymentBadRequestException.class);
        }

        @Test
        @DisplayName("예외: 이미 취소된 티켓")
        void throwsWhenTicketCanceled() {
            final Payment payment = createPayment();
            final Event event = createEventWithStartAt(LocalDateTime.now().plusDays(30));
            final Ticket canceledTicket = createTicket(TicketStatus.CANCELED);

            when(paymentRetriever.findPaymentByOrderId(ORDER_ID)).thenReturn(payment);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(ticketRetriever.findAllTicketsByOrderIdAndUserId(ORDER_ID, USER_ID))
                    .thenReturn(List.of(canceledTicket));

            assertThatThrownBy(() -> paymentService.cancelPayment(USER_ID, ORDER_ID))
                    .isInstanceOf(PaymentBadRequestException.class);
        }

        @Test
        @DisplayName("예외: 티켓 미존재")
        void throwsWhenTicketNotFound() {
            final Payment payment = createPayment();
            final Event event = createEventWithStartAt(LocalDateTime.now().plusDays(30));

            when(paymentRetriever.findPaymentByOrderId(ORDER_ID)).thenReturn(payment);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(ticketRetriever.findAllTicketsByOrderIdAndUserId(ORDER_ID, USER_ID))
                    .thenThrow(new TicketNotFoundException());

            assertThatThrownBy(() -> paymentService.cancelPayment(USER_ID, ORDER_ID))
                    .isInstanceOf(NotFoundPaymentException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // getPaymentConfirm (주요 예외 케이스만)
    // ══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getPaymentConfirm")
    class GetPaymentConfirmTest {

        @Test
        @DisplayName("예외: 예약 세션 미존재")
        void throwsWhenSessionNotFound() {
            when(reservationSessionRetriever.getValidatedReservationSession(eq(USER_ID), anyString(), any()))
                    .thenThrow(
                            new com.permitseoul.permitserver.domain.reservationsession.core.exception.ReservationSessionNotFoundException());

            assertThatThrownBy(() -> paymentService.getPaymentConfirm(
                    USER_ID, ORDER_ID, PAYMENT_KEY, new BigDecimal("60000"), "session-key"))
                    .isInstanceOf(NotFoundPaymentException.class);
        }

        @Test
        @DisplayName("예외: 예약 미존재 시 Redis 롤백 발생")
        void throwsWhenReservationNotFoundWithRollback() {
            final com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession session = new com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession(
                    1L, USER_ID, ORDER_ID, "session-key", false);
            final ReservationTicket reservationTicket = new ReservationTicket(1L, 10L, ORDER_ID, 1);

            when(reservationSessionRetriever.getValidatedReservationSession(eq(USER_ID), eq("session-key"), any()))
                    .thenReturn(session);
            when(reservationTicketRetriever.findAllByOrderId(ORDER_ID))
                    .thenReturn(List.of(reservationTicket));
            when(reservationRetriever.findReservationByOrderIdAndAmountAndUserId(ORDER_ID, new BigDecimal("60000"),
                    USER_ID))
                    .thenThrow(
                            new com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException());

            assertThatThrownBy(() -> paymentService.getPaymentConfirm(
                    USER_ID, ORDER_ID, PAYMENT_KEY, new BigDecimal("60000"), "session-key"))
                    .isInstanceOf(NotFoundPaymentException.class);

            // Redis 롤백이 실행되었는지 검증
            verify(redisManager).increment(anyString(), anyLong());
        }

        @Test
        @DisplayName("예외: 이벤트 미존재 시 Redis 롤백 발생")
        void throwsWhenEventNotFoundWithRollback() {
            final com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession session = new com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession(
                    1L, USER_ID, ORDER_ID, "session-key", false);
            final ReservationTicket reservationTicket = new ReservationTicket(1L, 10L, ORDER_ID, 1);
            final Reservation reservation = createReservation();

            when(reservationSessionRetriever.getValidatedReservationSession(eq(USER_ID), eq("session-key"), any()))
                    .thenReturn(session);
            when(reservationTicketRetriever.findAllByOrderId(ORDER_ID))
                    .thenReturn(List.of(reservationTicket));
            when(reservationRetriever.findReservationByOrderIdAndAmountAndUserId(ORDER_ID, new BigDecimal("60000"),
                    USER_ID))
                    .thenReturn(reservation);
            when(eventRetriever.findEventById(EVENT_ID))
                    .thenThrow(new EventNotfoundException());

            assertThatThrownBy(() -> paymentService.getPaymentConfirm(
                    USER_ID, ORDER_ID, PAYMENT_KEY, new BigDecimal("60000"), "session-key"))
                    .isInstanceOf(NotFoundPaymentException.class);

            // Redis 롤백이 실행되었는지 검증
            verify(redisManager).increment(anyString(), anyLong());
        }
    }
}
