package com.permitseoul.permitserver.domain.reservation.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservation.core.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("ReservationRetriever 테스트")
@ExtendWith(MockitoExtension.class)
class ReservationRetrieverTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationRetriever reservationRetriever;

    private static final String ORDER_ID = "ORDER-001";
    private static final BigDecimal TOTAL_AMOUNT = new BigDecimal("60000.00");
    private static final long USER_ID = 1L;

    private ReservationEntity createTestEntity() {
        final ReservationEntity entity = ReservationEntity.create(
                "테스트 예약", USER_ID, 10L, ORDER_ID, TOTAL_AMOUNT, null);
        ReflectionTestUtils.setField(entity, "reservationId", 100L);
        return entity;
    }

    @Nested
    @DisplayName("findReservationByOrderIdAndAmountAndUserId 메서드")
    class FindByOrderIdAndAmountAndUserId {

        @Test
        @DisplayName("존재하면 Reservation을 반환한다")
        void returnsReservationWhenFound() {
            // given
            given(reservationRepository.findByOrderIdAndTotalAmountAndUserId(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                    .willReturn(Optional.of(createTestEntity()));

            // when
            final Reservation result = reservationRetriever.findReservationByOrderIdAndAmountAndUserId(ORDER_ID,
                    TOTAL_AMOUNT, USER_ID);

            // then
            assertThat(result.getReservationId()).isEqualTo(100L);
            assertThat(result.getOrderId()).isEqualTo(ORDER_ID);
        }

        @Test
        @DisplayName("존재하지 않으면 ReservationNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(reservationRepository.findByOrderIdAndTotalAmountAndUserId(ORDER_ID, TOTAL_AMOUNT, USER_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reservationRetriever.findReservationByOrderIdAndAmountAndUserId(ORDER_ID,
                    TOTAL_AMOUNT, USER_ID))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findReservationById 메서드")
    class FindReservationById {

        @Test
        @DisplayName("존재하면 Reservation을 반환한다")
        void returnsReservationWhenFound() {
            // given
            given(reservationRepository.findById(100L)).willReturn(Optional.of(createTestEntity()));

            // when
            final Reservation result = reservationRetriever.findReservationById(100L);

            // then
            assertThat(result.getReservationId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("존재하지 않으면 ReservationNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(reservationRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reservationRetriever.findReservationById(999L))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findReservationEntityById 메서드")
    class FindReservationEntityById {

        @Test
        @DisplayName("존재하면 ReservationEntity를 반환한다")
        void returnsEntityWhenFound() {
            // given
            given(reservationRepository.findById(100L)).willReturn(Optional.of(createTestEntity()));

            // when
            final ReservationEntity result = reservationRetriever.findReservationEntityById(100L);

            // then
            assertThat(result.getReservationId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("존재하지 않으면 ReservationNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(reservationRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reservationRetriever.findReservationEntityById(999L))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findReservationByIdAndUserId 메서드")
    class FindReservationByIdAndUserId {

        @Test
        @DisplayName("존재하면 Reservation을 반환한다")
        void returnsReservationWhenFound() {
            // given
            given(reservationRepository.findByReservationIdAndUserId(100L, USER_ID))
                    .willReturn(Optional.of(createTestEntity()));

            // when
            final Reservation result = reservationRetriever.findReservationByIdAndUserId(100L, USER_ID);

            // then
            assertThat(result.getReservationId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("존재하지 않으면 ReservationNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(reservationRepository.findByReservationIdAndUserId(999L, USER_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reservationRetriever.findReservationByIdAndUserId(999L, USER_ID))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findReservationByOrderIdAndUserId 메서드")
    class FindReservationByOrderIdAndUserId {

        @Test
        @DisplayName("존재하면 Reservation을 반환한다")
        void returnsReservationWhenFound() {
            // given
            given(reservationRepository.findByOrderIdAndUserId(ORDER_ID, USER_ID))
                    .willReturn(Optional.of(createTestEntity()));

            // when
            final Reservation result = reservationRetriever.findReservationByOrderIdAndUserId(ORDER_ID, USER_ID);

            // then
            assertThat(result.getOrderId()).isEqualTo(ORDER_ID);
        }

        @Test
        @DisplayName("존재하지 않으면 ReservationNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(reservationRepository.findByOrderIdAndUserId("INVALID", USER_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reservationRetriever.findReservationByOrderIdAndUserId("INVALID", USER_ID))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }
}
