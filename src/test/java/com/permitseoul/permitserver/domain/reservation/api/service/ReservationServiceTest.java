package com.permitseoul.permitserver.domain.reservation.api.service;

import com.permitseoul.permitserver.domain.coupon.core.component.CouponRetriever;
import com.permitseoul.permitserver.domain.coupon.core.domain.Coupon;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponConflictException;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponNotfoundException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoRequest;
import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoResponse;
import com.permitseoul.permitserver.domain.reservation.api.exception.*;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationAndReservationTicketFacade;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionRetriever;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession;
import com.permitseoul.permitserver.domain.reservationsession.core.exception.ReservationSessionNotFoundException;
import com.permitseoul.permitserver.domain.ticketround.core.component.TicketRoundRetriever;
import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeInsufficientCountException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.domain.*;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.redis.RedisManager;
import org.springframework.test.util.ReflectionTestUtils;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService 테스트")
class ReservationServiceTest {

    @Mock
    private EventRetriever eventRetriever;
    @Mock
    private UserRetriever userRetriever;
    @Mock
    private TicketTypeRetriever ticketTypeRetriever;
    @Mock
    private CouponRetriever couponRetriever;
    @Mock
    private ReservationRetriever reservationRetriever;
    @Mock
    private TicketRoundRetriever ticketRoundRetriever;
    @Mock
    private RedisManager redisManager;
    @Mock
    private ReservationAndReservationTicketFacade reservationAndReservationTicketFacade;
    @Mock
    private ReservationSessionRetriever reservationSessionRetriever;

    @InjectMocks
    private ReservationService reservationService;

    // ── 공통 테스트 데이터 ──
    private static final long USER_ID = 1L;
    private static final long EVENT_ID = 100L;
    private static final String ORDER_ID = "ORDER-20260213-001";
    private static final String SESSION_KEY = "session-uuid-key";
    private static final String COUPON_CODE = "COUPON-2026";
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 2, 13, 14, 0);

    private Event createEvent() {
        return new Event(EVENT_ID, "테스트 이벤트", EventType.PERMIT, NOW.minusDays(1), NOW.plusDays(1),
                "서울", "라인업", "상세", 0, NOW.minusDays(7), NOW.plusDays(7), "CHECK-CODE");
    }

    private User createUser() {
        return new User(USER_ID, "홍길동", Gender.MALE, 25, "test@email.com", "social123", SocialType.KAKAO,
                UserRole.USER);
    }

    private TicketTypeEntity createTicketTypeEntity(final long ticketTypeId, final long ticketRoundId) {
        final TicketTypeEntity entity = TicketTypeEntity.create(ticketRoundId, "1일권", new BigDecimal("60000"), 100,
                NOW.minusDays(1), NOW.plusDays(1));
        ReflectionTestUtils.setField(entity, "ticketTypeId", ticketTypeId);
        return entity;
    }

    private TicketRoundEntity createTicketRoundEntity() {
        final TicketRoundEntity entity = TicketRoundEntity.create(EVENT_ID, "1차", NOW.minusDays(1), NOW.plusDays(1));
        ReflectionTestUtils.setField(entity, "ticketRoundId", 1L);
        return entity;
    }

    private Coupon createCoupon() {
        return new Coupon(1L, EVENT_ID, COUPON_CODE, 10, "테스트 쿠폰", false, null, NOW.minusDays(7));
    }

    private Reservation createReservation() {
        return new Reservation(1L, "[테스트 이벤트] 1일권x1", USER_ID, EVENT_ID, ORDER_ID,
                new BigDecimal("60000"), null, ReservationStatus.RESERVED, null);
    }

    private ReservationSession createReservationSession() {
        return new ReservationSession(1L, USER_ID, ORDER_ID, SESSION_KEY, false);
    }

    private List<ReservationInfoRequest.TicketTypeInfo> createTicketTypeInfos() {
        return List.of(new ReservationInfoRequest.TicketTypeInfo(10L, 1));
    }

    // ══════════════════════════════════════════════════════════════
    // getReservationInfo
    // ══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getReservationInfo")
    class GetReservationInfoTest {

        @Test
        @DisplayName("정상: 예약 정보 조회 성공")
        void success() {
            final ReservationSession session = createReservationSession();
            final User user = createUser();
            final Reservation reservation = createReservation();

            when(reservationSessionRetriever.getValidatedReservationSession(eq(USER_ID), eq(SESSION_KEY), any()))
                    .thenReturn(session);
            when(userRetriever.findUserById(USER_ID)).thenReturn(user);
            when(reservationRetriever.findReservationByOrderIdAndUserId(ORDER_ID, USER_ID))
                    .thenReturn(reservation);

            final ReservationInfoResponse result = reservationService.getReservationInfo(USER_ID, SESSION_KEY);

            assertThat(result.orderName()).isEqualTo("[테스트 이벤트] 1일권x1");
            assertThat(result.orderId()).isEqualTo(ORDER_ID);
            assertThat(result.userName()).isEqualTo("홍길동");
            assertThat(result.userEmail()).isEqualTo("test@email.com");
            assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("60000"));
            assertThat(result.customerKey()).isEqualTo("social123");
        }

        @Test
        @DisplayName("예외: 유효하지 않은 세션")
        void throwsWhenSessionNotFound() {
            when(reservationSessionRetriever.getValidatedReservationSession(eq(USER_ID), eq(SESSION_KEY), any()))
                    .thenThrow(new ReservationSessionNotFoundException());

            assertThatThrownBy(() -> reservationService.getReservationInfo(USER_ID, SESSION_KEY))
                    .isInstanceOf(NotfoundReservationException.class);
        }

        @Test
        @DisplayName("예외: 사용자 미존재")
        void throwsWhenUserNotFound() {
            final ReservationSession session = createReservationSession();
            when(reservationSessionRetriever.getValidatedReservationSession(eq(USER_ID), eq(SESSION_KEY), any()))
                    .thenReturn(session);
            when(userRetriever.findUserById(USER_ID)).thenThrow(new UserNotFoundException());

            assertThatThrownBy(() -> reservationService.getReservationInfo(USER_ID, SESSION_KEY))
                    .isInstanceOf(NotfoundReservationException.class);
        }

        @Test
        @DisplayName("예외: 예약 미존재")
        void throwsWhenReservationNotFound() {
            final ReservationSession session = createReservationSession();
            when(reservationSessionRetriever.getValidatedReservationSession(eq(USER_ID), eq(SESSION_KEY), any()))
                    .thenReturn(session);
            when(userRetriever.findUserById(USER_ID)).thenReturn(createUser());
            when(reservationRetriever.findReservationByOrderIdAndUserId(ORDER_ID, USER_ID))
                    .thenThrow(new ReservationNotFoundException());

            assertThatThrownBy(() -> reservationService.getReservationInfo(USER_ID, SESSION_KEY))
                    .isInstanceOf(NotfoundReservationException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // saveReservation
    // ══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("saveReservation")
    class SaveReservationTest {

        @Test
        @DisplayName("정상: 쿠폰 없이 예약 저장 성공")
        void successWithoutCoupon() {
            final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos = createTicketTypeInfos();
            final TicketTypeEntity ticketTypeEntity = createTicketTypeEntity(10L, 1L);
            final TicketRoundEntity ticketRoundEntity = createTicketRoundEntity();
            final Event event = createEvent();

            when(ticketTypeRetriever.findAllTicketTypeEntityByIds(List.of(10L)))
                    .thenReturn(List.of(ticketTypeEntity));
            doNothing().when(userRetriever).validExistUserById(USER_ID);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(ticketRoundRetriever.findTicketRoundEntityById(anyLong())).thenReturn(ticketRoundEntity);
            when(redisManager.decrement(anyString(), anyLong())).thenReturn(49L);
            when(reservationAndReservationTicketFacade.saveReservationWithTicketAndSession(
                    anyString(), eq(USER_ID), eq(EVENT_ID), eq(ORDER_ID),
                    any(BigDecimal.class), isNull(), eq(ticketTypeInfos)))
                    .thenReturn(SESSION_KEY);

            final String result = reservationService.saveReservation(
                    USER_ID, EVENT_ID, null, new BigDecimal("60000"), ORDER_ID, ticketTypeInfos, NOW);

            assertThat(result).isEqualTo(SESSION_KEY);
            verify(reservationAndReservationTicketFacade).saveReservationWithTicketAndSession(
                    anyString(), eq(USER_ID), eq(EVENT_ID), eq(ORDER_ID),
                    any(BigDecimal.class), isNull(), eq(ticketTypeInfos));
        }

        @Test
        @DisplayName("예외: 이벤트 미존재")
        void throwsWhenEventNotFound() {
            final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos = createTicketTypeInfos();
            final TicketTypeEntity ticketTypeEntity = createTicketTypeEntity(10L, 1L);
            when(ticketTypeRetriever.findAllTicketTypeEntityByIds(List.of(10L)))
                    .thenReturn(List.of(ticketTypeEntity));
            doNothing().when(userRetriever).validExistUserById(USER_ID);
            when(eventRetriever.findEventById(EVENT_ID)).thenThrow(new EventNotfoundException());

            assertThatThrownBy(() -> reservationService.saveReservation(
                    USER_ID, EVENT_ID, null, new BigDecimal("60000"), ORDER_ID, ticketTypeInfos, NOW))
                    .isInstanceOf(NotfoundReservationException.class);
        }

        @Test
        @DisplayName("예외: 사용자 미존재")
        void throwsWhenUserNotFound() {
            final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos = createTicketTypeInfos();
            final TicketTypeEntity ticketTypeEntity = createTicketTypeEntity(10L, 1L);
            when(ticketTypeRetriever.findAllTicketTypeEntityByIds(List.of(10L)))
                    .thenReturn(List.of(ticketTypeEntity));
            doThrow(new UserNotFoundException()).when(userRetriever).validExistUserById(USER_ID);

            assertThatThrownBy(() -> reservationService.saveReservation(
                    USER_ID, EVENT_ID, null, new BigDecimal("60000"), ORDER_ID, ticketTypeInfos, NOW))
                    .isInstanceOf(NotfoundReservationException.class);
        }

        @Test
        @DisplayName("예외: 쿠폰 코드 미존재")
        void throwsWhenCouponNotFound() {
            final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos = createTicketTypeInfos();
            final TicketTypeEntity ticketTypeEntity = createTicketTypeEntity(10L, 1L);
            final Event event = createEvent();

            when(ticketTypeRetriever.findAllTicketTypeEntityByIds(List.of(10L)))
                    .thenReturn(List.of(ticketTypeEntity));
            doNothing().when(userRetriever).validExistUserById(USER_ID);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(couponRetriever.findValidCouponByCodeAndEvent(COUPON_CODE, EVENT_ID))
                    .thenThrow(new CouponNotfoundException());

            assertThatThrownBy(() -> reservationService.saveReservation(
                    USER_ID, EVENT_ID, COUPON_CODE, new BigDecimal("54000"), ORDER_ID, ticketTypeInfos, NOW))
                    .isInstanceOf(NotfoundReservationException.class);
        }

        @Test
        @DisplayName("예외: 이미 사용된 쿠폰")
        void throwsWhenCouponAlreadyUsed() {
            final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos = createTicketTypeInfos();
            final TicketTypeEntity ticketTypeEntity = createTicketTypeEntity(10L, 1L);
            final Event event = createEvent();

            when(ticketTypeRetriever.findAllTicketTypeEntityByIds(List.of(10L)))
                    .thenReturn(List.of(ticketTypeEntity));
            doNothing().when(userRetriever).validExistUserById(USER_ID);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(couponRetriever.findValidCouponByCodeAndEvent(COUPON_CODE, EVENT_ID))
                    .thenThrow(new CouponConflictException());

            assertThatThrownBy(() -> reservationService.saveReservation(
                    USER_ID, EVENT_ID, COUPON_CODE, new BigDecimal("54000"), ORDER_ID, ticketTypeInfos, NOW))
                    .isInstanceOf(ConflictReservationException.class);
        }

        @Test
        @DisplayName("예외: 판매 기간 만료 라운드")
        void throwsWhenTicketRoundExpired() {
            final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos = createTicketTypeInfos();
            // 미래 날짜의 라운드 (아직 판매 시작 전)
            final TicketRoundEntity expiredRound = TicketRoundEntity.create(EVENT_ID, "1차",
                    NOW.minusDays(10), NOW.minusDays(5));
            final TicketTypeEntity ticketTypeEntity = createTicketTypeEntity(10L, 1L);
            final Event event = createEvent();

            when(ticketTypeRetriever.findAllTicketTypeEntityByIds(List.of(10L)))
                    .thenReturn(List.of(ticketTypeEntity));
            doNothing().when(userRetriever).validExistUserById(USER_ID);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(ticketRoundRetriever.findTicketRoundEntityById(anyLong())).thenReturn(expiredRound);

            assertThatThrownBy(() -> reservationService.saveReservation(
                    USER_ID, EVENT_ID, null, new BigDecimal("60000"), ORDER_ID, ticketTypeInfos, NOW))
                    .isInstanceOf(ExpiredReservationException.class);
        }

        @Test
        @DisplayName("예외: Redis 재고 부족")
        void throwsWhenRedisInsufficientTicket() {
            final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos = createTicketTypeInfos();
            final TicketTypeEntity ticketTypeEntity = createTicketTypeEntity(10L, 1L);
            final TicketRoundEntity ticketRoundEntity = createTicketRoundEntity();
            final Event event = createEvent();

            when(ticketTypeRetriever.findAllTicketTypeEntityByIds(List.of(10L)))
                    .thenReturn(List.of(ticketTypeEntity));
            doNothing().when(userRetriever).validExistUserById(USER_ID);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(ticketRoundRetriever.findTicketRoundEntityById(anyLong())).thenReturn(ticketRoundEntity);
            // Redis 재고가 -1 반환 → 부족
            when(redisManager.decrement(anyString(), anyLong())).thenReturn(-1L);

            assertThatThrownBy(() -> reservationService.saveReservation(
                    USER_ID, EVENT_ID, null, new BigDecimal("60000"), ORDER_ID, ticketTypeInfos, NOW))
                    .isInstanceOf(InSufficientReservationException.class);

            // Redis 롤백 검증
            verify(redisManager).increment(anyString(), anyLong());
        }

        @Test
        @DisplayName("예외: 금액 불일치")
        void throwsWhenAmountMismatch() {
            final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos = createTicketTypeInfos();
            final TicketTypeEntity ticketTypeEntity = createTicketTypeEntity(10L, 1L);
            final TicketRoundEntity ticketRoundEntity = createTicketRoundEntity();
            final Event event = createEvent();

            when(ticketTypeRetriever.findAllTicketTypeEntityByIds(List.of(10L)))
                    .thenReturn(List.of(ticketTypeEntity));
            doNothing().when(userRetriever).validExistUserById(USER_ID);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(ticketRoundRetriever.findTicketRoundEntityById(anyLong())).thenReturn(ticketRoundEntity);

            // 금액 불일치: 실제 60000 but 요청 99999
            assertThatThrownBy(() -> reservationService.saveReservation(
                    USER_ID, EVENT_ID, null, new BigDecimal("99999"), ORDER_ID, ticketTypeInfos, NOW))
                    .isInstanceOf(ReservationBadRequestException.class);
        }
    }
}
