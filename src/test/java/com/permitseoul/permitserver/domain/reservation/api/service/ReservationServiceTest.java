package com.permitseoul.permitserver.domain.reservation.api.service;

import com.permitseoul.permitserver.domain.coupon.core.component.CouponRetriever;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponConflictException;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponNotfoundException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoRequest;
import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoResponse;
import com.permitseoul.permitserver.domain.reservation.api.exception.ConflictReservationException;
import com.permitseoul.permitserver.domain.reservation.api.exception.NotfoundReservationException;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationSaver;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketSaver;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock ReservationSaver reservationSaver;
    @Mock ReservationTicketSaver reservationTicketSaver;
    @Mock EventRetriever eventRetriever;
    @Mock UserRetriever userRetriever;
    @Mock TicketTypeRetriever ticketTypeRetriever;
    @Mock CouponRetriever couponRetriever;
    @Mock ReservationRetriever reservationRetriever;

    @InjectMocks ReservationService reservationService;

    @Nested
    @DisplayName("saveReservation() 단위 테스트")
    class SaveReservation {
        @Test
        @DisplayName("정상 예약 저장")
        void success() {
            long userId = 1L;
            long eventId = 2L;
            String couponCode = "COUPON";
            BigDecimal totalAmount = BigDecimal.valueOf(10000);
            String orderId = "ORDER-1";
            List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos = List.of(
                    new ReservationInfoRequest.TicketTypeInfo(10L, 2)
            );
            willDoNothing().given(userRetriever).isExistUserByUserId(userId);
            willDoNothing().given(eventRetriever).isExistByEventId(eventId);
            willDoNothing().given(ticketTypeRetriever).isExistByTicketTypeId(10L);
            willDoNothing().given(couponRetriever).isExistCoupon(couponCode);
            willDoNothing().given(couponRetriever).isCouponValid(couponCode);
            Reservation reservation = mock(Reservation.class);
            given(reservationSaver.saveReservation(userId, eventId, orderId, totalAmount, couponCode)).willReturn(reservation);
            given(reservation.getOrderId()).willReturn(orderId);
            willDoNothing().given(reservationTicketSaver).saveReservationTicket(anyLong(), anyString(), anyInt());
            assertThatCode(() -> reservationService.saveReservation(userId, eventId, couponCode, totalAmount, orderId, ticketTypeInfos)).doesNotThrowAnyException();
        }
        @Test
        @DisplayName("이벤트 없음 예외")
        void eventNotFound() {
            willThrow(EventNotfoundException.class).given(eventRetriever).isExistByEventId(anyLong());
            assertThatThrownBy(() -> reservationService.saveReservation(1L, 2L, null, BigDecimal.TEN, "ORDER", List.of())).isInstanceOf(NotfoundReservationException.class);
        }
        @Test
        @DisplayName("유저 없음 예외")
        void userNotFound() {
            willThrow(UserNotFoundException.class).given(userRetriever).isExistUserByUserId(anyLong());
            assertThatThrownBy(() -> reservationService.saveReservation(1L, 2L, null, BigDecimal.TEN, "ORDER", List.of())).isInstanceOf(NotfoundReservationException.class);
        }
        @Test
        @DisplayName("쿠폰 없음 예외")
        void couponNotFound() {
            willDoNothing().given(userRetriever).isExistUserByUserId(anyLong());
            willDoNothing().given(eventRetriever).isExistByEventId(anyLong());
            willDoNothing().given(ticketTypeRetriever).isExistByTicketTypeId(anyLong());
            willThrow(CouponNotfoundException.class).given(couponRetriever).isExistCoupon(anyString());
            assertThatThrownBy(() -> reservationService.saveReservation(1L, 2L, "COUPON", BigDecimal.TEN, "ORDER", List.of(new ReservationInfoRequest.TicketTypeInfo(10L, 1))))
                    .isInstanceOf(NotfoundReservationException.class);
        }
        @Test
        @DisplayName("쿠폰 중복 사용 예외")
        void couponConflict() {
            willDoNothing().given(userRetriever).isExistUserByUserId(anyLong());
            willDoNothing().given(eventRetriever).isExistByEventId(anyLong());
            willDoNothing().given(ticketTypeRetriever).isExistByTicketTypeId(anyLong());
            willDoNothing().given(couponRetriever).isExistCoupon(anyString());
            willThrow(CouponConflictException.class).given(couponRetriever).isCouponValid(anyString());
            assertThatThrownBy(() -> reservationService.saveReservation(1L, 2L, "COUPON", BigDecimal.TEN, "ORDER", List.of(new ReservationInfoRequest.TicketTypeInfo(10L, 1))))
                    .isInstanceOf(ConflictReservationException.class);
        }
    }

    @Nested
    @DisplayName("getReservationInfo() 단위 테스트")
    class GetReservationInfo {
        @Test
        @DisplayName("정상 예약 정보 조회")
        void success() {
            long userId = 1L;
            String orderId = "ORDER-1";
            User user = mock(User.class);
            Reservation reservation = mock(Reservation.class);
            Event event = mock(Event.class);
            given(userRetriever.findUserById(userId)).willReturn(user);
            given(reservationRetriever.findReservationEntityByOrderIdAndUserId(orderId, userId)).willReturn(reservation);
            given(reservation.getEventId()).willReturn(10L);
            given(eventRetriever.findEventById(10L)).willReturn(event);
            given(event.getName()).willReturn("이벤트명");
            given(reservation.getOrderId()).willReturn(orderId);
            given(user.getName()).willReturn("홍길동");
            given(user.getEmail()).willReturn("hong@test.com");
            given(reservation.getTotalAmount()).willReturn(BigDecimal.TEN);
            given(user.getSocialId()).willReturn("social-1");
            assertThatCode(() -> reservationService.getReservationInfo(userId, orderId)).doesNotThrowAnyException();
        }
        @Test
        @DisplayName("이벤트 없음 예외")
        void eventNotFound() {
            User user = mock(User.class);
            Reservation reservation = mock(Reservation.class);
            given(userRetriever.findUserById(anyLong())).willReturn(user);
            given(reservationRetriever.findReservationEntityByOrderIdAndUserId(anyString(), anyLong())).willReturn(reservation);
            given(reservation.getEventId()).willReturn(10L);
            given(eventRetriever.findEventById(anyLong())).willThrow(EventNotfoundException.class);
            assertThatThrownBy(() -> reservationService.getReservationInfo(1L, "ORDER")).isInstanceOf(NotfoundReservationException.class);
        }
        @Test
        @DisplayName("유저 없음 예외")
        void userNotFound() {
            given(userRetriever.findUserById(anyLong())).willThrow(UserNotFoundException.class);
            assertThatThrownBy(() -> reservationService.getReservationInfo(1L, "ORDER")).isInstanceOf(NotfoundReservationException.class);
        }
        @Test
        @DisplayName("예약 없음 예외")
        void reservationNotFound() {
            User user = mock(User.class);
            given(userRetriever.findUserById(anyLong())).willReturn(user);
            given(reservationRetriever.findReservationEntityByOrderIdAndUserId(anyString(), anyLong())).willThrow(ReservationNotFoundException.class);
            assertThatThrownBy(() -> reservationService.getReservationInfo(1L, "ORDER")).isInstanceOf(NotfoundReservationException.class);
        }
    }
} 