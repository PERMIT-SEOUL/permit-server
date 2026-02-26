package com.permitseoul.permitserver.domain.ticket.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentRetriever;
import com.permitseoul.permitserver.domain.ticket.api.dto.res.DoorValidateUserTicket;
import com.permitseoul.permitserver.domain.ticket.api.dto.res.EventTicketInfoResponse;
import com.permitseoul.permitserver.domain.ticket.api.dto.res.UserBuyTicketInfoResponse;
import com.permitseoul.permitserver.domain.ticket.api.exception.ConflictTicketException;
import com.permitseoul.permitserver.domain.ticket.api.exception.DateTicketException;
import com.permitseoul.permitserver.domain.ticket.api.exception.IllegalTicketException;
import com.permitseoul.permitserver.domain.ticket.api.exception.NotFoundTicketException;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketRetriever;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketUpdater;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import com.permitseoul.permitserver.domain.ticket.core.exception.TicketNotFoundException;
import com.permitseoul.permitserver.domain.ticketround.core.component.TicketRoundRetriever;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException;
import com.permitseoul.permitserver.global.redis.RedisManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketService 테스트")
class TicketServiceTest {

    @Mock
    private TicketRoundRetriever ticketRoundRetriever;
    @Mock
    private TicketTypeRetriever ticketTypeRetriever;
    @Mock
    private TicketRetriever ticketRetriever;
    @Mock
    private EventRetriever eventRetriever;
    @Mock
    private PaymentRetriever paymentRetriever;
    @Mock
    private RedisManager redisManager;
    @Mock
    private TicketUpdater ticketUpdater;

    @InjectMocks
    private TicketService ticketService;

    // ── 공통 테스트 데이터 ──
    private static final String TICKET_CODE = "TKT-20260213-ABC123";
    private static final String CHECK_CODE = "EVENT-CHECK-001";
    private static final long TICKET_TYPE_ID = 10L;
    private static final long EVENT_ID = 100L;
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 2, 13, 14, 0);
    private static final LocalDateTime TICKET_START = NOW.minusHours(1);
    private static final LocalDateTime TICKET_END = NOW.plusHours(5);

    private TicketEntity createTicketEntity(final TicketStatus status) {
        final TicketEntity entity = TicketEntity.create(1L, "ORDER-001", TICKET_TYPE_ID, EVENT_ID, TICKET_CODE,
                new BigDecimal("60000"));
        if (status != TicketStatus.RESERVED) {
            entity.updateTicketStatus(status);
        }
        return entity;
    }

    private Ticket createTicket(final TicketStatus status) {
        return Ticket.builder()
                .ticketId(1L)
                .userId(1L)
                .orderId("ORDER-001")
                .ticketTypeId(TICKET_TYPE_ID)
                .eventId(EVENT_ID)
                .ticketCode(TICKET_CODE)
                .status(status)
                .createdAt(NOW)
                .ticketPrice(new BigDecimal("60000"))
                .build();
    }

    private TicketType createTicketType(final LocalDateTime startAt, final LocalDateTime endAt) {
        return new TicketType(TICKET_TYPE_ID, 1L, "1일권", new BigDecimal("60000"), 100, 50, startAt, endAt);
    }

    private Event createEvent() {
        return new Event(EVENT_ID, "테스트 이벤트", EventType.PERMIT, NOW.minusDays(1), NOW.plusDays(1),
                "서울", "라인업", "상세", 0, NOW.minusDays(7), NOW.plusDays(7), CHECK_CODE);
    }

    // ══════════════════════════════════════════════════════════════
    // confirmTicketByStaffCode
    // ══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("confirmTicketByStaffCode")
    class ConfirmTicketByStaffCodeTest {

        @Test
        @DisplayName("정상: 유효한 티켓 코드와 체크코드로 입장 확인 성공")
        void success() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.RESERVED);
            final TicketType ticketType = createTicketType(TICKET_START, TICKET_END);
            final Event event = createEvent();

            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID)).thenReturn(ticketType);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);

            ticketService.confirmTicketByStaffCode(TICKET_CODE, CHECK_CODE);

            verify(ticketUpdater).updateTicketStatus(ticketEntity, TicketStatus.USED);
        }

        @Test
        @DisplayName("예외: 존재하지 않는 티켓 코드")
        void throwsWhenTicketNotFound() {
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE))
                    .thenThrow(new TicketNotFoundException());

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCode(TICKET_CODE, CHECK_CODE))
                    .isInstanceOf(NotFoundTicketException.class);
        }

        @Test
        @DisplayName("예외: 존재하지 않는 티켓 타입")
        void throwsWhenTicketTypeNotFound() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.RESERVED);
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID))
                    .thenThrow(new TicketTypeNotfoundException());

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCode(TICKET_CODE, CHECK_CODE))
                    .isInstanceOf(NotFoundTicketException.class);
        }

        @Test
        @DisplayName("예외: 존재하지 않는 이벤트")
        void throwsWhenEventNotFound() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.RESERVED);
            final TicketType ticketType = createTicketType(TICKET_START, TICKET_END);
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID)).thenReturn(ticketType);
            when(eventRetriever.findEventById(EVENT_ID)).thenThrow(new EventNotfoundException());

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCode(TICKET_CODE, CHECK_CODE))
                    .isInstanceOf(NotFoundTicketException.class);
        }

        @Test
        @DisplayName("예외: 이미 사용된 티켓")
        void throwsWhenTicketUsed() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.USED);
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCode(TICKET_CODE, CHECK_CODE))
                    .isInstanceOf(ConflictTicketException.class);
        }

        @Test
        @DisplayName("예외: 취소된 티켓")
        void throwsWhenTicketCanceled() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.CANCELED);
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCode(TICKET_CODE, CHECK_CODE))
                    .isInstanceOf(IllegalTicketException.class);
        }

        @Test
        @DisplayName("예외: 체크코드 불일치")
        void throwsWhenCheckCodeMismatch() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.RESERVED);
            final TicketType ticketType = createTicketType(TICKET_START, TICKET_END);
            final Event event = createEvent();
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID)).thenReturn(ticketType);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCode(TICKET_CODE, "WRONG-CODE"))
                    .isInstanceOf(IllegalTicketException.class);
        }

        @Test
        @DisplayName("예외: 티켓 이용 기간 외")
        void throwsWhenOutOfDateRange() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.RESERVED);
            // 미래 날짜로 설정하여 현재 시간이 ticketStartAt 이전이 되도록 함
            final TicketType ticketType = createTicketType(
                    LocalDateTime.now().plusDays(10),
                    LocalDateTime.now().plusDays(11));
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID)).thenReturn(ticketType);

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCode(TICKET_CODE, CHECK_CODE))
                    .isInstanceOf(DateTicketException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // confirmTicketByStaffCamera
    // ══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("confirmTicketByStaffCamera")
    class ConfirmTicketByStaffCameraTest {

        @Test
        @DisplayName("정상: 카메라로 티켓 확인 성공")
        void success() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.RESERVED);
            final TicketType ticketType = createTicketType(TICKET_START, TICKET_END);
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID)).thenReturn(ticketType);

            ticketService.confirmTicketByStaffCamera(TICKET_CODE);

            verify(ticketUpdater).updateTicketStatus(ticketEntity, TicketStatus.USED);
        }

        @Test
        @DisplayName("예외: 존재하지 않는 티켓")
        void throwsWhenTicketNotFound() {
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE))
                    .thenThrow(new TicketNotFoundException());

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCamera(TICKET_CODE))
                    .isInstanceOf(NotFoundTicketException.class);
        }

        @Test
        @DisplayName("예외: 존재하지 않는 티켓 타입")
        void throwsWhenTicketTypeNotFound() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.RESERVED);
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID))
                    .thenThrow(new TicketTypeNotfoundException());

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCamera(TICKET_CODE))
                    .isInstanceOf(NotFoundTicketException.class);
        }

        @Test
        @DisplayName("예외: 이미 사용된 티켓")
        void throwsWhenTicketUsed() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.USED);
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCamera(TICKET_CODE))
                    .isInstanceOf(ConflictTicketException.class);
        }

        @Test
        @DisplayName("예외: 티켓 이용 기간 외")
        void throwsWhenOutOfDateRange() {
            final TicketEntity ticketEntity = createTicketEntity(TicketStatus.RESERVED);
            final TicketType ticketType = createTicketType(
                    LocalDateTime.now().plusDays(10),
                    LocalDateTime.now().plusDays(11));
            when(ticketRetriever.findTicketEntityByTicketCode(TICKET_CODE)).thenReturn(ticketEntity);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID)).thenReturn(ticketType);

            assertThatThrownBy(() -> ticketService.confirmTicketByStaffCamera(TICKET_CODE))
                    .isInstanceOf(DateTicketException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // validateUserTicket
    // ══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("validateUserTicket")
    class ValidateUserTicketTest {

        @Test
        @DisplayName("정상: 유효한 티켓 검증 후 DoorValidateUserTicket 반환")
        void success() {
            final Ticket ticket = createTicket(TicketStatus.RESERVED);
            final TicketType ticketType = createTicketType(TICKET_START, TICKET_END);
            final Event event = createEvent();
            when(ticketRetriever.findTicketByTicketCode(TICKET_CODE)).thenReturn(ticket);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID)).thenReturn(ticketType);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);

            final DoorValidateUserTicket result = ticketService.validateUserTicket(TICKET_CODE);

            assertThat(result.eventName()).isEqualTo("테스트 이벤트");
            assertThat(result.ticketName()).isEqualTo("1일권");
            assertThat(result.ticketStartDate()).isEqualTo(TICKET_START);
            assertThat(result.ticketEndDate()).isEqualTo(TICKET_END);
        }

        @Test
        @DisplayName("예외: 존재하지 않는 티켓")
        void throwsWhenTicketNotFound() {
            when(ticketRetriever.findTicketByTicketCode(TICKET_CODE))
                    .thenThrow(new TicketNotFoundException());

            assertThatThrownBy(() -> ticketService.validateUserTicket(TICKET_CODE))
                    .isInstanceOf(NotFoundTicketException.class);
        }

        @Test
        @DisplayName("예외: 존재하지 않는 티켓 타입")
        void throwsWhenTicketTypeNotFound() {
            final Ticket ticket = createTicket(TicketStatus.RESERVED);
            when(ticketRetriever.findTicketByTicketCode(TICKET_CODE)).thenReturn(ticket);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID))
                    .thenThrow(new TicketTypeNotfoundException());

            assertThatThrownBy(() -> ticketService.validateUserTicket(TICKET_CODE))
                    .isInstanceOf(NotFoundTicketException.class);
        }

        @Test
        @DisplayName("예외: 존재하지 않는 이벤트")
        void throwsWhenEventNotFound() {
            final Ticket ticket = createTicket(TicketStatus.RESERVED);
            final TicketType ticketType = createTicketType(TICKET_START, TICKET_END);
            when(ticketRetriever.findTicketByTicketCode(TICKET_CODE)).thenReturn(ticket);
            when(ticketTypeRetriever.findTicketTypeById(TICKET_TYPE_ID)).thenReturn(ticketType);
            when(eventRetriever.findEventById(EVENT_ID)).thenThrow(new EventNotfoundException());

            assertThatThrownBy(() -> ticketService.validateUserTicket(TICKET_CODE))
                    .isInstanceOf(NotFoundTicketException.class);
        }

        @Test
        @DisplayName("예외: 이미 사용된 티켓")
        void throwsWhenTicketUsed() {
            final Ticket ticket = createTicket(TicketStatus.USED);
            when(ticketRetriever.findTicketByTicketCode(TICKET_CODE)).thenReturn(ticket);

            assertThatThrownBy(() -> ticketService.validateUserTicket(TICKET_CODE))
                    .isInstanceOf(ConflictTicketException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // getUserBuyTicketInfo
    // ══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getUserBuyTicketInfo")
    class GetUserBuyTicketInfoTest {

        @Test
        @DisplayName("userId가 null이면 빈 리스트 반환")
        void returnsEmptyWhenUserIdNull() {
            final UserBuyTicketInfoResponse result = ticketService.getUserBuyTicketInfo(null);

            assertThat(result.orders()).isEmpty();
            verifyNoInteractions(ticketRetriever);
        }

        @Test
        @DisplayName("티켓이 없으면 빈 리스트 반환")
        void returnsEmptyWhenNoTickets() {
            when(ticketRetriever.findAllTicketsByUserId(1L)).thenReturn(List.of());

            final UserBuyTicketInfoResponse result = ticketService.getUserBuyTicketInfo(1L);

            assertThat(result.orders()).isEmpty();
        }

        @Test
        @DisplayName("예외: 티켓 타입 미존재")
        void throwsWhenTicketTypeNotFound() {
            final Ticket ticket = createTicket(TicketStatus.RESERVED);
            when(ticketRetriever.findAllTicketsByUserId(1L)).thenReturn(List.of(ticket));
            when(ticketTypeRetriever.findAllTicketTypeById(List.of(TICKET_TYPE_ID)))
                    .thenThrow(new TicketTypeNotfoundException());

            assertThatThrownBy(() -> ticketService.getUserBuyTicketInfo(1L))
                    .isInstanceOf(NotFoundTicketException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // getEventTicketInfo
    // ══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getEventTicketInfo")
    class GetEventTicketInfoTest {

        @Test
        @DisplayName("판매 가능한 라운드가 없으면 빈 리스트 반환")
        void returnsEmptyWhenNoRounds() {
            when(ticketRoundRetriever.findSalesOrSalesEndTicketRoundByEventId(eq(EVENT_ID), any()))
                    .thenReturn(List.of());

            final EventTicketInfoResponse result = ticketService.getEventTicketInfo(EVENT_ID, NOW);

            assertThat(result.rounds()).isEmpty();
        }

        @Test
        @DisplayName("예외: 라운드에 해당하는 티켓 타입이 없음")
        void throwsWhenTicketTypeNotFound() {
            final TicketRound round = new TicketRound(1L, EVENT_ID, "1차", NOW.minusDays(1), NOW.plusDays(1));
            when(ticketRoundRetriever.findSalesOrSalesEndTicketRoundByEventId(eq(EVENT_ID), any()))
                    .thenReturn(List.of(round));
            when(ticketTypeRetriever.findTicketTypeListByRoundIdList(List.of(1L)))
                    .thenThrow(new TicketTypeNotfoundException());

            assertThatThrownBy(() -> ticketService.getEventTicketInfo(EVENT_ID, NOW))
                    .isInstanceOf(NotFoundTicketException.class);
        }
    }
}
