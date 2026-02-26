package com.permitseoul.permitserver.domain.reservation.core.domain;

import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.global.exception.IllegalEnumTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Reservation & ReservationEntity 테스트")
class ReservationEntityTest {

    private static final String RESERVATION_NAME = "테스트 예약";
    private static final long USER_ID = 1L;
    private static final long EVENT_ID = 10L;
    private static final String ORDER_ID = "ORDER-20260119-001";
    private static final BigDecimal TOTAL_AMOUNT = new BigDecimal("60000.00");
    private static final String COUPON_CODE = "COUPON-001";

    private ReservationEntity createTestEntity() {
        return ReservationEntity.create(RESERVATION_NAME, USER_ID, EVENT_ID, ORDER_ID, TOTAL_AMOUNT, COUPON_CODE);
    }

    @Nested
    @DisplayName("ReservationEntity.create 메서드")
    class Create {

        @Test
        @DisplayName("정상적인 값으로 ReservationEntity를 생성한다")
        void createsReservationEntitySuccessfully() {
            // when
            final ReservationEntity entity = createTestEntity();

            // then
            assertThat(entity.getReservationName()).isEqualTo(RESERVATION_NAME);
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getEventId()).isEqualTo(EVENT_ID);
            assertThat(entity.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(entity.getTotalAmount()).isEqualByComparingTo(TOTAL_AMOUNT);
            assertThat(entity.getCouponCode()).isEqualTo(COUPON_CODE);
        }

        @Test
        @DisplayName("초기 status는 RESERVED이다")
        void initialStatusIsReserved() {
            // when
            final ReservationEntity entity = createTestEntity();

            // then
            assertThat(entity.getStatus()).isEqualTo(ReservationStatus.RESERVED);
        }

        @Test
        @DisplayName("초기 tossPaymentResponseAt은 null이다")
        void initialTossPaymentResponseAtIsNull() {
            // when
            final ReservationEntity entity = createTestEntity();

            // then
            assertThat(entity.getTossPaymentResponseAt()).isNull();
        }
    }

    @Nested
    @DisplayName("updateReservationStatus 메서드")
    class UpdateReservationStatus {

        @Test
        @DisplayName("RESERVED → PAYMENT_SUCCESS 전이 성공")
        void transitionsFromReservedToPaymentSuccess() {
            // given
            final ReservationEntity entity = createTestEntity();

            // when
            entity.updateReservationStatus(ReservationStatus.PAYMENT_SUCCESS);

            // then
            assertThat(entity.getStatus()).isEqualTo(ReservationStatus.PAYMENT_SUCCESS);
        }

        @Test
        @DisplayName("RESERVED → PAYMENT_FAILED 전이 성공")
        void transitionsFromReservedToPaymentFailed() {
            // given
            final ReservationEntity entity = createTestEntity();

            // when
            entity.updateReservationStatus(ReservationStatus.PAYMENT_FAILED);

            // then
            assertThat(entity.getStatus()).isEqualTo(ReservationStatus.PAYMENT_FAILED);
        }

        @Test
        @DisplayName("RESERVED → TICKET_ISSUED 전이 불가 → IllegalEnumTransitionException")
        void throwsExceptionForInvalidTransition() {
            // given
            final ReservationEntity entity = createTestEntity();

            // when & then
            assertThatThrownBy(() -> entity.updateReservationStatus(ReservationStatus.TICKET_ISSUED))
                    .isInstanceOf(IllegalEnumTransitionException.class);
        }

        @Test
        @DisplayName("PAYMENT_FAILED 상태에서는 어떤 전이도 불가하다")
        void paymentFailedCannotTransition() {
            // given
            final ReservationEntity entity = createTestEntity();
            entity.updateReservationStatus(ReservationStatus.PAYMENT_FAILED);

            // when & then
            assertThatThrownBy(() -> entity.updateReservationStatus(ReservationStatus.RESERVED))
                    .isInstanceOf(IllegalEnumTransitionException.class);
        }

        @Test
        @DisplayName("정상 흐름: RESERVED → PAYMENT_SUCCESS → TICKET_ISSUED → PAYMENT_CANCELED")
        void fullLifecycleTransition() {
            // given
            final ReservationEntity entity = createTestEntity();

            // when
            entity.updateReservationStatus(ReservationStatus.PAYMENT_SUCCESS);
            entity.updateReservationStatus(ReservationStatus.TICKET_ISSUED);
            entity.updateReservationStatus(ReservationStatus.PAYMENT_CANCELED);

            // then
            assertThat(entity.getStatus()).isEqualTo(ReservationStatus.PAYMENT_CANCELED);
        }
    }

    @Nested
    @DisplayName("updateTossPaymentResponseTime 메서드")
    class UpdateTossPaymentResponseTime {

        @Test
        @DisplayName("tossPaymentResponseAt을 정상 업데이트한다")
        void updatesTossPaymentResponseTime() {
            // given
            final ReservationEntity entity = createTestEntity();
            final LocalDateTime responseTime = LocalDateTime.of(2026, 1, 19, 17, 5);

            // when
            entity.updateTossPaymentResponseTime(responseTime);

            // then
            assertThat(entity.getTossPaymentResponseAt()).isEqualTo(responseTime);
        }
    }

    @Nested
    @DisplayName("Reservation.fromEntity 메서드")
    class FromEntity {

        @Test
        @DisplayName("Entity의 모든 필드가 Domain 객체로 정확히 매핑된다")
        void mapsAllFieldsCorrectly() {
            // given
            final ReservationEntity entity = createTestEntity();
            ReflectionTestUtils.setField(entity, "reservationId", 100L);

            // when
            final Reservation reservation = Reservation.fromEntity(entity);

            // then
            assertThat(reservation.getReservationId()).isEqualTo(100L);
            assertThat(reservation.getReservationName()).isEqualTo(RESERVATION_NAME);
            assertThat(reservation.getUserId()).isEqualTo(USER_ID);
            assertThat(reservation.getEventId()).isEqualTo(EVENT_ID);
            assertThat(reservation.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(reservation.getTotalAmount()).isEqualByComparingTo(TOTAL_AMOUNT);
            assertThat(reservation.getCouponCode()).isEqualTo(COUPON_CODE);
            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
            assertThat(reservation.getTossPaymentResponseAt()).isNull();
        }
    }
}
