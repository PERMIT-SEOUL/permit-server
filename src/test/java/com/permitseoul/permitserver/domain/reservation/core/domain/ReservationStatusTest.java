package com.permitseoul.permitserver.domain.reservation.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReservationStatus 테스트")
class ReservationStatusTest {

    @Nested
    @DisplayName("열거값 기본 검증")
    class EnumBasics {

        @Test
        @DisplayName("열거값은 5개이다")
        void hasFiveValues() {
            assertThat(ReservationStatus.values()).hasSize(5);
        }

        @ParameterizedTest(name = "valueOf(\"{0}\")으로 변환 가능하다")
        @EnumSource(ReservationStatus.class)
        @DisplayName("모든 열거값이 valueOf로 변환 가능하다")
        void valueOfWorksForAll(final ReservationStatus status) {
            assertThat(ReservationStatus.valueOf(status.name())).isEqualTo(status);
        }
    }

    @Nested
    @DisplayName("canTransitionTo - 허용되는 전이")
    class AllowedTransitions {

        @Test
        @DisplayName("RESERVED → PAYMENT_SUCCESS 전이 가능")
        void reservedToPaymentSuccess() {
            assertThat(ReservationStatus.RESERVED.canTransitionTo(ReservationStatus.PAYMENT_SUCCESS)).isTrue();
        }

        @Test
        @DisplayName("RESERVED → PAYMENT_FAILED 전이 가능")
        void reservedToPaymentFailed() {
            assertThat(ReservationStatus.RESERVED.canTransitionTo(ReservationStatus.PAYMENT_FAILED)).isTrue();
        }

        @Test
        @DisplayName("PAYMENT_SUCCESS → TICKET_ISSUED 전이 가능")
        void paymentSuccessToTicketIssued() {
            assertThat(ReservationStatus.PAYMENT_SUCCESS.canTransitionTo(ReservationStatus.TICKET_ISSUED)).isTrue();
        }

        @Test
        @DisplayName("TICKET_ISSUED → PAYMENT_CANCELED 전이 가능")
        void ticketIssuedToPaymentCanceled() {
            assertThat(ReservationStatus.TICKET_ISSUED.canTransitionTo(ReservationStatus.PAYMENT_CANCELED)).isTrue();
        }
    }

    @Nested
    @DisplayName("canTransitionTo - 불허되는 전이")
    class DisallowedTransitions {

        private static Stream<Arguments> disallowedTransitions() {
            return Stream.of(
                    // RESERVED에서 불가
                    Arguments.of(ReservationStatus.RESERVED, ReservationStatus.RESERVED),
                    Arguments.of(ReservationStatus.RESERVED, ReservationStatus.TICKET_ISSUED),
                    Arguments.of(ReservationStatus.RESERVED, ReservationStatus.PAYMENT_CANCELED),
                    // PAYMENT_SUCCESS에서 불가
                    Arguments.of(ReservationStatus.PAYMENT_SUCCESS, ReservationStatus.RESERVED),
                    Arguments.of(ReservationStatus.PAYMENT_SUCCESS, ReservationStatus.PAYMENT_SUCCESS),
                    Arguments.of(ReservationStatus.PAYMENT_SUCCESS, ReservationStatus.PAYMENT_FAILED),
                    Arguments.of(ReservationStatus.PAYMENT_SUCCESS, ReservationStatus.PAYMENT_CANCELED),
                    // TICKET_ISSUED에서 불가
                    Arguments.of(ReservationStatus.TICKET_ISSUED, ReservationStatus.RESERVED),
                    Arguments.of(ReservationStatus.TICKET_ISSUED, ReservationStatus.PAYMENT_SUCCESS),
                    Arguments.of(ReservationStatus.TICKET_ISSUED, ReservationStatus.PAYMENT_FAILED),
                    Arguments.of(ReservationStatus.TICKET_ISSUED, ReservationStatus.TICKET_ISSUED),
                    // PAYMENT_FAILED에서 모두 불가
                    Arguments.of(ReservationStatus.PAYMENT_FAILED, ReservationStatus.RESERVED),
                    Arguments.of(ReservationStatus.PAYMENT_FAILED, ReservationStatus.PAYMENT_SUCCESS),
                    Arguments.of(ReservationStatus.PAYMENT_FAILED, ReservationStatus.PAYMENT_FAILED),
                    Arguments.of(ReservationStatus.PAYMENT_FAILED, ReservationStatus.TICKET_ISSUED),
                    Arguments.of(ReservationStatus.PAYMENT_FAILED, ReservationStatus.PAYMENT_CANCELED),
                    // PAYMENT_CANCELED에서 모두 불가
                    Arguments.of(ReservationStatus.PAYMENT_CANCELED, ReservationStatus.RESERVED),
                    Arguments.of(ReservationStatus.PAYMENT_CANCELED, ReservationStatus.PAYMENT_SUCCESS),
                    Arguments.of(ReservationStatus.PAYMENT_CANCELED, ReservationStatus.PAYMENT_FAILED),
                    Arguments.of(ReservationStatus.PAYMENT_CANCELED, ReservationStatus.TICKET_ISSUED),
                    Arguments.of(ReservationStatus.PAYMENT_CANCELED, ReservationStatus.PAYMENT_CANCELED));
        }

        @ParameterizedTest(name = "{0} → {1} 전이 불가")
        @MethodSource("disallowedTransitions")
        @DisplayName("불허되는 상태 전이는 false를 반환한다")
        void disallowedTransitionReturnsFalse(final ReservationStatus from, final ReservationStatus to) {
            assertThat(from.canTransitionTo(to)).isFalse();
        }
    }
}
