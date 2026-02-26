package com.permitseoul.permitserver.domain.ticket.core.domain;

import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Ticket & TicketEntity 테스트")
class TicketEntityTest {

    private static final long USER_ID = 1L;
    private static final String ORDER_ID = "ORDER-20260119-001";
    private static final long TICKET_TYPE_ID = 5L;
    private static final long EVENT_ID = 10L;
    private static final String TICKET_CODE = "ABC1234567";
    private static final BigDecimal TICKET_PRICE = new BigDecimal("60000");

    private TicketEntity createTestEntity() {
        return TicketEntity.create(USER_ID, ORDER_ID, TICKET_TYPE_ID, EVENT_ID, TICKET_CODE, TICKET_PRICE);
    }

    @Nested
    @DisplayName("TicketEntity.create 메서드")
    class Create {

        @Test
        @DisplayName("정상적인 값으로 TicketEntity를 생성한다")
        void createsTicketEntitySuccessfully() {
            // when
            final TicketEntity entity = createTestEntity();

            // then
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(entity.getTicketTypeId()).isEqualTo(TICKET_TYPE_ID);
            assertThat(entity.getEventId()).isEqualTo(EVENT_ID);
            assertThat(entity.getTicketCode()).isEqualTo(TICKET_CODE);
            assertThat(entity.getTicketPrice()).isEqualByComparingTo(TICKET_PRICE);
        }

        @Test
        @DisplayName("초기 status는 RESERVED이다")
        void initialStatusIsReserved() {
            // when
            final TicketEntity entity = createTestEntity();

            // then
            assertThat(entity.getStatus()).isEqualTo(TicketStatus.RESERVED);
        }

        @Test
        @DisplayName("생성 직후 ticketId는 null이다 (@GeneratedValue)")
        void ticketIdIsNullAfterCreate() {
            // when
            final TicketEntity entity = createTestEntity();

            // then
            assertThat(entity.getTicketId()).isNull();
        }
    }

    @Nested
    @DisplayName("updateTicketStatus 메서드")
    class UpdateTicketStatus {

        @Test
        @DisplayName("USED로 변경하면 usedTime이 설정된다")
        void setsUsedTimeWhenStatusIsUsed() {
            // given
            final TicketEntity entity = createTestEntity();

            // when
            entity.updateTicketStatus(TicketStatus.USED);

            // then
            assertThat(entity.getStatus()).isEqualTo(TicketStatus.USED);
            assertThat(entity.getUsedTime()).isNotNull();
        }

        @Test
        @DisplayName("CANCELED로 변경하면 usedTime은 설정되지 않는다")
        void doesNotSetUsedTimeWhenStatusIsCanceled() {
            // given
            final TicketEntity entity = createTestEntity();

            // when
            entity.updateTicketStatus(TicketStatus.CANCELED);

            // then
            assertThat(entity.getStatus()).isEqualTo(TicketStatus.CANCELED);
            assertThat(entity.getUsedTime()).isNull();
        }

        @Test
        @DisplayName("RESERVED에서 USED로 변경 후 상태값이 올바르다")
        void transitionsFromReservedToUsed() {
            // given
            final TicketEntity entity = createTestEntity();
            assertThat(entity.getStatus()).isEqualTo(TicketStatus.RESERVED);

            // when
            entity.updateTicketStatus(TicketStatus.USED);

            // then
            assertThat(entity.getStatus()).isEqualTo(TicketStatus.USED);
        }
    }

    @Nested
    @DisplayName("Ticket.fromEntity 메서드")
    class FromEntity {

        @Test
        @DisplayName("Entity의 모든 필드가 Domain 객체로 정확히 매핑된다")
        void mapsAllFieldsCorrectly() {
            // given
            final TicketEntity entity = createTestEntity();
            ReflectionTestUtils.setField(entity, "ticketId", 100L);

            // when
            final Ticket ticket = Ticket.fromEntity(entity);

            // then
            assertThat(ticket.getTicketId()).isEqualTo(100L);
            assertThat(ticket.getUserId()).isEqualTo(USER_ID);
            assertThat(ticket.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(ticket.getTicketTypeId()).isEqualTo(TICKET_TYPE_ID);
            assertThat(ticket.getEventId()).isEqualTo(EVENT_ID);
            assertThat(ticket.getTicketCode()).isEqualTo(TICKET_CODE);
            assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESERVED);
            assertThat(ticket.getTicketPrice()).isEqualByComparingTo(TICKET_PRICE);
        }
    }
}
