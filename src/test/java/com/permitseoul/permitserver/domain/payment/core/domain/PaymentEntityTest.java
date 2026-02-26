package com.permitseoul.permitserver.domain.payment.core.domain;

import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Payment & PaymentEntity 테스트")
class PaymentEntityTest {

    private static final long RESERVATION_ID = 1L;
    private static final String ORDER_ID = "ORDER-20260119-001";
    private static final long EVENT_ID = 10L;
    private static final String PAYMENT_KEY = "toss_pay_key_123";
    private static final BigDecimal TOTAL_AMOUNT = new BigDecimal("60000.00");
    private static final Currency CURRENCY = Currency.KRW;
    private static final LocalDateTime REQUESTED_AT = LocalDateTime.of(2026, 1, 19, 17, 0);
    private static final LocalDateTime APPROVED_AT = LocalDateTime.of(2026, 1, 19, 17, 1);

    @Nested
    @DisplayName("PaymentEntity.create 메서드")
    class Create {

        @Test
        @DisplayName("정상적인 값으로 PaymentEntity를 생성한다")
        void createsPaymentEntitySuccessfully() {
            // when
            final PaymentEntity entity = PaymentEntity.create(
                    RESERVATION_ID, ORDER_ID, EVENT_ID, PAYMENT_KEY,
                    TOTAL_AMOUNT, CURRENCY, REQUESTED_AT, APPROVED_AT);

            // then
            assertThat(entity.getReservationId()).isEqualTo(RESERVATION_ID);
            assertThat(entity.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(entity.getEventId()).isEqualTo(EVENT_ID);
            assertThat(entity.getPaymentKey()).isEqualTo(PAYMENT_KEY);
            assertThat(entity.getTotalAmount()).isEqualByComparingTo(TOTAL_AMOUNT);
            assertThat(entity.getCurrency()).isEqualTo(CURRENCY);
            assertThat(entity.getRequestedAt()).isEqualTo(REQUESTED_AT);
            assertThat(entity.getApprovedAt()).isEqualTo(APPROVED_AT);
        }

        @Test
        @DisplayName("생성 직후 paymentId는 null이다 (@GeneratedValue)")
        void paymentIdIsNullAfterCreate() {
            // when
            final PaymentEntity entity = PaymentEntity.create(
                    RESERVATION_ID, ORDER_ID, EVENT_ID, PAYMENT_KEY,
                    TOTAL_AMOUNT, CURRENCY, REQUESTED_AT, APPROVED_AT);

            // then
            assertThat(entity.getPaymentId()).isNull();
        }

        @Test
        @DisplayName("approvedAt이 null이어도 생성 가능하다")
        void createsWithNullApprovedAt() {
            // when
            final PaymentEntity entity = PaymentEntity.create(
                    RESERVATION_ID, ORDER_ID, EVENT_ID, PAYMENT_KEY,
                    TOTAL_AMOUNT, CURRENCY, REQUESTED_AT, null);

            // then
            assertThat(entity.getApprovedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Payment.fromEntity 메서드")
    class FromEntity {

        @Test
        @DisplayName("Entity의 모든 필드가 Domain 객체로 정확히 매핑된다")
        void mapsAllFieldsCorrectly() {
            // given
            final PaymentEntity entity = PaymentEntity.create(
                    RESERVATION_ID, ORDER_ID, EVENT_ID, PAYMENT_KEY,
                    TOTAL_AMOUNT, CURRENCY, REQUESTED_AT, APPROVED_AT);
            ReflectionTestUtils.setField(entity, "paymentId", 100L);

            // when
            final Payment payment = Payment.fromEntity(entity);

            // then
            assertThat(payment.getPaymentId()).isEqualTo(100L);
            assertThat(payment.getReservationId()).isEqualTo(RESERVATION_ID);
            assertThat(payment.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(payment.getEventId()).isEqualTo(EVENT_ID);
            assertThat(payment.getPaymentKey()).isEqualTo(PAYMENT_KEY);
            assertThat(payment.getTotalAmount()).isEqualByComparingTo(TOTAL_AMOUNT);
            assertThat(payment.getCurrency()).isEqualTo(CURRENCY);
            assertThat(payment.getRequestedAt()).isEqualTo(REQUESTED_AT);
            assertThat(payment.getApprovedAt()).isEqualTo(APPROVED_AT);
        }
    }
}
