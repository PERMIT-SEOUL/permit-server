package com.permitseoul.permitserver.domain.payment.core.component;

import com.permitseoul.permitserver.domain.payment.core.domain.Currency;
import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import com.permitseoul.permitserver.domain.payment.core.exception.PaymentNotFoundException;
import com.permitseoul.permitserver.domain.payment.core.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("PaymentRetriever 테스트")
@ExtendWith(MockitoExtension.class)
class PaymentRetrieverTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentRetriever paymentRetriever;

    private PaymentEntity createTestEntity() {
        final PaymentEntity entity = PaymentEntity.create(
                1L, "ORDER-001", 10L, "pay_key_123",
                new BigDecimal("60000"), Currency.KRW,
                LocalDateTime.of(2026, 1, 19, 17, 0),
                LocalDateTime.of(2026, 1, 19, 17, 1));
        ReflectionTestUtils.setField(entity, "paymentId", 100L);
        return entity;
    }

    @Nested
    @DisplayName("findPaymentByOrderId 메서드")
    class FindPaymentByOrderId {

        @Test
        @DisplayName("존재하는 orderId로 조회하면 Payment를 반환한다")
        void returnsPaymentWhenFound() {
            // given
            given(paymentRepository.findByOrderId("ORDER-001")).willReturn(Optional.of(createTestEntity()));

            // when
            final Payment result = paymentRetriever.findPaymentByOrderId("ORDER-001");

            // then
            assertThat(result.getPaymentId()).isEqualTo(100L);
            assertThat(result.getOrderId()).isEqualTo("ORDER-001");
        }

        @Test
        @DisplayName("존재하지 않는 orderId로 조회하면 PaymentNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(paymentRepository.findByOrderId("INVALID")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentRetriever.findPaymentByOrderId("INVALID"))
                    .isInstanceOf(PaymentNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findPaymentEntityByOrderId 메서드")
    class FindPaymentEntityByOrderId {

        @Test
        @DisplayName("존재하는 orderId로 조회하면 PaymentEntity를 반환한다")
        void returnsEntityWhenFound() {
            // given
            given(paymentRepository.findByOrderId("ORDER-001")).willReturn(Optional.of(createTestEntity()));

            // when
            final PaymentEntity result = paymentRetriever.findPaymentEntityByOrderId("ORDER-001");

            // then
            assertThat(result.getPaymentId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("존재하지 않는 orderId로 조회하면 PaymentNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(paymentRepository.findByOrderId("INVALID")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentRetriever.findPaymentEntityByOrderId("INVALID"))
                    .isInstanceOf(PaymentNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findPaymentByOrderIdIn 메서드")
    class FindPaymentByOrderIdIn {

        @Test
        @DisplayName("존재하는 orderIds로 조회하면 Payment 리스트를 반환한다")
        void returnsPaymentListWhenFound() {
            // given
            given(paymentRepository.findByOrderIdIn(Set.of("ORDER-001")))
                    .willReturn(List.of(createTestEntity()));

            // when
            final List<Payment> result = paymentRetriever.findPaymentByOrderIdIn(Set.of("ORDER-001"));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOrderId()).isEqualTo("ORDER-001");
        }

        @Test
        @DisplayName("빈 리스트면 PaymentNotFoundException을 던진다")
        void throwsExceptionWhenEmpty() {
            // given
            given(paymentRepository.findByOrderIdIn(Set.of("INVALID")))
                    .willReturn(Collections.emptyList());

            // when & then
            assertThatThrownBy(() -> paymentRetriever.findPaymentByOrderIdIn(Set.of("INVALID")))
                    .isInstanceOf(PaymentNotFoundException.class);
        }
    }
}
