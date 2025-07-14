package com.permitseoul.permitserver.domain.reservation.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.payment.api.client.TossPaymentClient;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentResponse;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentSaver;
import com.permitseoul.permitserver.domain.payment.core.domain.Currency;
import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.payment.core.domain.PaymentStatus;
import com.permitseoul.permitserver.domain.reservation.api.TossProperties;
import com.permitseoul.permitserver.domain.reservation.api.dto.PaymentConfirmResponse;
import com.permitseoul.permitserver.domain.reservation.api.exception.ConflictReservationException;
import com.permitseoul.permitserver.domain.reservation.api.exception.NotfoundReservationException;
import com.permitseoul.permitserver.domain.reservation.api.exception.TicketAlgorithmException;
import com.permitseoul.permitserver.domain.reservation.api.exception.TossPaymentConfirmException;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketRetriever;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketSaver;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRetriever reservationRetriever;
    @Mock
    private EventRetriever eventRetriever;
    @Mock
    private TossPaymentClient tossPaymentClient;
    @Mock
    private PaymentSaver paymentSaver;
    @Mock
    private ReservationTicketRetriever reservationTicketRetriever;
    @Mock
    private TicketTypeRetriever ticketTypeRetriever;
    @Mock
    private TicketSaver ticketSaver;
    @Mock
    private TicketTypeRepository ticketTypeRepository;
    @Mock
    private TossProperties tossProperties;

    @InjectMocks
    private ReservationService reservationService;

    private static final long USER_ID = 1L;
    private static final String ORDER_ID = "order123";
    private static final String PAYMENT_KEY = "paymentKey123";
    private static final BigDecimal TOTAL_AMOUNT = new BigDecimal("50000");
    private static final long EVENT_ID = 1L;
    private static final long RESERVATION_ID = 1L;
    private static final long TICKET_TYPE_ID = 1L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reservationService, "authorizationHeader", "Basic dGVzdC1zZWNyZXQta2V5Og==");
    }

    @Nested
    @DisplayName("1. 예약 정보 조회 테스트")
    class ReservationRetrievalTest {
        
        @Test
        @DisplayName("예약 정보 조회 성공")
        void should_RetrieveReservation_When_ValidOrderIdAndAmount() throws JsonProcessingException {
            // given
            Reservation reservation = createReservation();
            given(reservationRetriever.findReservationByOrderIdAndAmount(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                    .willReturn(reservation);
            
            // 나머지 의존성들을 최소한으로 Mock 설정
            setupMinimalMocksForSuccess();

            // when
            reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            verify(reservationRetriever).findReservationByOrderIdAndAmount(ORDER_ID, TOTAL_AMOUNT, USER_ID);
        }

        @Test
        @DisplayName("예약 정보 조회 실패 - 예약 없음")
        void should_ThrowException_When_ReservationNotFound() {
            // given
            given(reservationRetriever.findReservationByOrderIdAndAmount(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                    .willThrow(new ReservationNotFoundException());

            // when & then
            assertThatThrownBy(() -> reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT))
                    .isInstanceOf(NotfoundReservationException.class);
        }
    }

    @Nested
    @DisplayName("2. 이벤트 정보 조회 테스트")
    class EventRetrievalTest {
        
        @Test
        @DisplayName("이벤트 정보 조회 성공")
        void should_RetrieveEvent_When_ValidEventId() throws JsonProcessingException {
            // given
            setupMinimalMocksForSuccess();
            Event event = createEvent();
            given(eventRetriever.findEventById(EVENT_ID)).willReturn(event);

            // when
            reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            verify(eventRetriever).findEventById(EVENT_ID);
        }

        @Test
        @DisplayName("이벤트 정보 조회 실패 - 이벤트 없음")
        void should_ThrowException_When_EventNotFound() {
            // given
            given(reservationRetriever.findReservationByOrderIdAndAmount(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                    .willReturn(createReservation());
            given(eventRetriever.findEventById(EVENT_ID))
                    .willThrow(new com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException());

            // when & then
            assertThatThrownBy(() -> reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT))
                    .isInstanceOf(NotfoundReservationException.class);
        }
    }

    @Nested
    @DisplayName("3. 토스 결제 확인 테스트")
    class TossPaymentTest {
        
        @Test
        @DisplayName("토스 결제 확인 성공")
        void should_ConfirmPayment_When_ValidPaymentRequest() throws JsonProcessingException {
            // given
            setupMinimalMocksForSuccess();
            PaymentResponse paymentResponse = createPaymentResponse();
            given(tossPaymentClient.purchaseConfirm(anyString(), any())).willReturn(paymentResponse);

            // when
            reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            verify(tossPaymentClient).purchaseConfirm(eq("Basic dGVzdC1zZWNyZXQta2V5Og=="), any());
        }

        @Test
        @DisplayName("토스 결제 확인 실패 - API 오류")
        void should_ThrowException_When_TossApiError() throws JsonProcessingException {
            // given
            given(reservationRetriever.findReservationByOrderIdAndAmount(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                    .willReturn(createReservation());
            given(eventRetriever.findEventById(EVENT_ID)).willReturn(createEvent());
            
            FeignException feignException = mock(FeignException.class);
            given(feignException.contentUTF8()).willReturn("{\"code\":\"PAYMENT_ERROR\",\"message\":\"결제 오류\"}");
            given(tossPaymentClient.purchaseConfirm(anyString(), any())).willThrow(feignException);

            // when & then
            assertThatThrownBy(() -> reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT))
                    .isInstanceOf(TossPaymentConfirmException.class);
        }
    }

    @Nested
    @DisplayName("4. 결제 정보 저장 테스트")
    class PaymentSavingTest {
        
        @Test
        @DisplayName("결제 정보 저장 성공")
        void should_SavePayment_When_ValidPaymentResponse() throws JsonProcessingException {
            // given
            Reservation reservation = createReservation();
            Event event = createEvent();
            PaymentResponse paymentResponse = createPaymentResponse();
            Payment savedPayment = createPayment();
            List<ReservationTicket> reservationTickets = createReservationTickets();
            TicketTypeEntity ticketTypeEntity = createTicketTypeEntity();

            given(reservationRetriever.findReservationByOrderIdAndAmount(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                    .willReturn(reservation);
            given(eventRetriever.findEventById(EVENT_ID)).willReturn(event);
            given(tossPaymentClient.purchaseConfirm(anyString(), any())).willReturn(paymentResponse);
            given(paymentSaver.savePayment(anyLong(), anyString(), anyLong(), anyString(), any(BigDecimal.class), any(Currency.class), anyString(), anyString()))
                    .willReturn(savedPayment);
            given(reservationTicketRetriever.findAllByOrderId(ORDER_ID)).willReturn(reservationTickets);
            given(ticketTypeRetriever.findTicketTypeEntityById(TICKET_TYPE_ID)).willReturn(ticketTypeEntity);

            // when
            reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            verify(paymentSaver).savePayment(RESERVATION_ID, ORDER_ID, EVENT_ID, PAYMENT_KEY, TOTAL_AMOUNT, Currency.KRW, "2024-01-01T00:00:00", "2024-01-01T00:00:00");
        }
    }

    @Nested
    @DisplayName("5. 예약 티켓 조회 테스트")
    class ReservationTicketRetrievalTest {
        
        @Test
        @DisplayName("예약 티켓 조회 성공")
        void should_RetrieveReservationTickets_When_ValidOrderId() throws JsonProcessingException {
            // given
            setupMinimalMocksForSuccess();
            List<ReservationTicket> reservationTickets = createReservationTickets();
            given(reservationTicketRetriever.findAllByOrderId(ORDER_ID)).willReturn(reservationTickets);

            // when
            reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            verify(reservationTicketRetriever).findAllByOrderId(ORDER_ID);
        }
    }

    @Nested
    @DisplayName("6. 티켓 타입 조회 및 개수 차감 테스트")
    class TicketTypeTest {
        
        @Test
        @DisplayName("티켓 타입 조회 성공")
        void should_RetrieveTicketType_When_ValidTicketTypeId() throws JsonProcessingException {
            // given
            setupMinimalMocksForSuccess();
            TicketTypeEntity ticketType = createTicketTypeEntity();
            given(ticketTypeRetriever.findTicketTypeEntityById(TICKET_TYPE_ID)).willReturn(ticketType);

            // when
            reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            verify(ticketTypeRetriever).findTicketTypeEntityById(TICKET_TYPE_ID);
        }

        @Test
        @DisplayName("티켓 타입 조회 실패 - 티켓 타입 없음")
        void should_ThrowException_When_TicketTypeNotFound() {
            // given
            setupMocksUntilTicketTypeRetrieval();
            given(ticketTypeRetriever.findTicketTypeEntityById(TICKET_TYPE_ID))
                    .willThrow(new com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException());

            // when & then
            assertThatThrownBy(() -> reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT))
                    .isInstanceOf(NotfoundReservationException.class);
        }

        @Test
        @DisplayName("티켓 개수 차감 성공")
        void should_DecreaseTicketCount_When_SufficientCount() throws JsonProcessingException {
            // given
            setupMinimalMocksForSuccess();
            TicketTypeEntity ticketType = createTicketTypeEntity();
            given(ticketTypeRetriever.findTicketTypeEntityById(TICKET_TYPE_ID)).willReturn(ticketType);

            // when
            reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            verify(ticketType).decreaseRemainCount(2);  // ReservationTicket count = 2
        }

        @Test
        @DisplayName("티켓 개수 차감 실패 - 개수 부족")
        void should_ThrowException_When_InsufficientTicketCount() {
            // given
            setupMocksUntilTicketTypeRetrieval();
            TicketTypeEntity ticketType = createTicketTypeEntity();
            given(ticketTypeRetriever.findTicketTypeEntityById(TICKET_TYPE_ID)).willReturn(ticketType);
            doThrow(new com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeInsufficientCountException())
                    .when(ticketType).decreaseRemainCount(2);

            // when & then
            assertThatThrownBy(() -> reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT))
                    .isInstanceOf(ConflictReservationException.class);
        }
    }

    @Nested
    @DisplayName("7. 티켓 생성 테스트")
    class TicketCreationTest {
        
        @Test
        @DisplayName("단일 티켓 타입 티켓 생성 성공")
        void should_CreateTickets_When_SingleTicketType() throws JsonProcessingException {
            // given
            setupMinimalMocksForSuccess();

            // when
            reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            ArgumentCaptor<List<Ticket>> ticketCaptor = ArgumentCaptor.forClass(List.class);
            verify(ticketSaver).saveTickets(ticketCaptor.capture());
            
            List<Ticket> savedTickets = ticketCaptor.getValue();
            assertThat(savedTickets).hasSize(2);  // count = 2
            
            savedTickets.forEach(ticket -> {
                assertThat(ticket.getUserId()).isEqualTo(USER_ID);
                assertThat(ticket.getTicketTypeId()).isEqualTo(TICKET_TYPE_ID);
                assertThat(ticket.getOrderId()).isEqualTo(ORDER_ID);
                assertThat(ticket.getEventId()).isEqualTo(EVENT_ID);
                assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESERVED);
                assertThat(ticket.isUsed()).isFalse();
                assertThat(ticket.getTicketCode()).isNotNull();
            });
        }

        @Test
        @DisplayName("다중 티켓 타입 티켓 생성 성공")
        void should_CreateTickets_When_MultipleTicketTypes() throws JsonProcessingException {
            // given
            setupMocksForMultipleTicketTypes();

            // when
            reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            ArgumentCaptor<List<Ticket>> ticketCaptor = ArgumentCaptor.forClass(List.class);
            verify(ticketSaver).saveTickets(ticketCaptor.capture());
            
            List<Ticket> savedTickets = ticketCaptor.getValue();
            assertThat(savedTickets).hasSize(5);  // 2 + 3 = 5개
            
            // 티켓 타입별 개수 확인
            long type1Count = savedTickets.stream()
                    .filter(ticket -> ticket.getTicketTypeId() == TICKET_TYPE_ID)
                    .count();
            long type2Count = savedTickets.stream()
                    .filter(ticket -> ticket.getTicketTypeId() == TICKET_TYPE_ID + 1)
                    .count();
            
            assertThat(type1Count).isEqualTo(2);
            assertThat(type2Count).isEqualTo(3);
        }

        @Test
        @DisplayName("티켓 생성 실패 - 알고리즘 오류")
        void should_ThrowException_When_TicketCreationError() {
            // given
            setupMocksUntilTicketSaving();
            doThrow(new com.permitseoul.permitserver.global.exception.AlgorithmException())
                    .when(ticketSaver).saveTickets(anyList());

            // when & then
            assertThatThrownBy(() -> reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT))
                    .isInstanceOf(TicketAlgorithmException.class);
        }
    }

    @Nested
    @DisplayName("8. 응답 생성 테스트")
    class ResponseCreationTest {
        
        @Test
        @DisplayName("응답 생성 성공")
        void should_CreateResponse_When_AllStepsSucceed() throws JsonProcessingException {
            // given
            setupMinimalMocksForSuccess();
            Event event = createEvent();
            given(eventRetriever.findEventById(EVENT_ID)).willReturn(event);

            // when
            PaymentConfirmResponse response = reservationService.getPaymentConfirm(USER_ID, ORDER_ID, PAYMENT_KEY, TOTAL_AMOUNT);

            // then
            assertThat(response.eventName()).isEqualTo("테스트 이벤트");
            assertThat(response.eventDate()).isNotNull();
        }
    }

    // 헬퍼 메소드들
    private void setupMinimalMocksForSuccess() {
        given(reservationRetriever.findReservationByOrderIdAndAmount(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                .willReturn(createReservation());
        given(eventRetriever.findEventById(EVENT_ID)).willReturn(createEvent());
        given(tossPaymentClient.purchaseConfirm(anyString(), any())).willReturn(createPaymentResponse());
        given(paymentSaver.savePayment(anyLong(), anyString(), anyLong(), anyString(), any(BigDecimal.class), any(Currency.class), anyString(), anyString()))
                .willReturn(createPayment());
        given(reservationTicketRetriever.findAllByOrderId(ORDER_ID)).willReturn(createReservationTickets());
        given(ticketTypeRetriever.findTicketTypeEntityById(TICKET_TYPE_ID)).willReturn(createTicketTypeEntity());
    }

    private void setupMocksUntilTicketTypeRetrieval() {
        given(reservationRetriever.findReservationByOrderIdAndAmount(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                .willReturn(createReservation());
        given(eventRetriever.findEventById(EVENT_ID)).willReturn(createEvent());
        given(tossPaymentClient.purchaseConfirm(anyString(), any())).willReturn(createPaymentResponse());
        given(paymentSaver.savePayment(anyLong(), anyString(), anyLong(), anyString(), any(BigDecimal.class), any(Currency.class), anyString(), anyString()))
                .willReturn(createPayment());
        given(reservationTicketRetriever.findAllByOrderId(ORDER_ID)).willReturn(createReservationTickets());
    }

    private void setupMocksUntilTicketSaving() {
        setupMocksUntilTicketTypeRetrieval();
        given(ticketTypeRetriever.findTicketTypeEntityById(TICKET_TYPE_ID)).willReturn(createTicketTypeEntity());
    }

    private void setupMocksForMultipleTicketTypes() {
        given(reservationRetriever.findReservationByOrderIdAndAmount(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                .willReturn(createReservation());
        given(eventRetriever.findEventById(EVENT_ID)).willReturn(createEvent());
        given(tossPaymentClient.purchaseConfirm(anyString(), any())).willReturn(createPaymentResponse());
        given(paymentSaver.savePayment(anyLong(), anyString(), anyLong(), anyString(), any(BigDecimal.class), any(Currency.class), anyString(), anyString()))
                .willReturn(createPayment());
        
        List<ReservationTicket> multipleTickets = Arrays.asList(
                new ReservationTicket(1L, TICKET_TYPE_ID, ORDER_ID, 2),
                new ReservationTicket(2L, TICKET_TYPE_ID + 1, ORDER_ID, 3)
        );
        given(reservationTicketRetriever.findAllByOrderId(ORDER_ID)).willReturn(multipleTickets);
        given(ticketTypeRetriever.findTicketTypeEntityById(TICKET_TYPE_ID)).willReturn(createTicketTypeEntity());
        given(ticketTypeRetriever.findTicketTypeEntityById(TICKET_TYPE_ID + 1)).willReturn(createTicketTypeEntity());
    }

    // 테스트 데이터 생성 메소드들
    private Reservation createReservation() {
        return new Reservation(RESERVATION_ID, USER_ID, EVENT_ID, ORDER_ID, TOTAL_AMOUNT, null, ReservationStatus.SUCCESS);
    }

    private Event createEvent() {
        return new Event(EVENT_ID, "테스트 이벤트", com.permitseoul.permitserver.domain.event.core.domain.EventType.PERMIT,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), "테스트 장소", "테스트 라인업", 
                "테스트 설명", 18, true, "TEST123", LocalDateTime.now());
    }

    private PaymentResponse createPaymentResponse() {
        return new PaymentResponse(PAYMENT_KEY, ORDER_ID, Currency.KRW, TOTAL_AMOUNT, "2024-01-01T00:00:00", "2024-01-01T00:00:00", null);
    }

    private Payment createPayment() {
        return new Payment(1L, RESERVATION_ID, ORDER_ID, EVENT_ID, PAYMENT_KEY, TOTAL_AMOUNT, PaymentStatus.SUCCESS, Currency.KRW, "2024-01-01T00:00:00", "2024-01-01T00:00:00");
    }

    private List<ReservationTicket> createReservationTickets() {
        return Arrays.asList(new ReservationTicket(1L, TICKET_TYPE_ID, ORDER_ID, 2));
    }

    private TicketTypeEntity createTicketTypeEntity() {
        return mock(TicketTypeEntity.class);
    }
} 