package com.permitseoul.permitserver.domain.ticket.core.component;

import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import com.permitseoul.permitserver.domain.ticket.core.exception.TicketNotFoundException;
import com.permitseoul.permitserver.domain.ticket.core.repository.TicketRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("TicketRetriever 테스트")
@ExtendWith(MockitoExtension.class)
class TicketRetrieverTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketRetriever ticketRetriever;

    private TicketEntity createTestEntity() {
        final TicketEntity entity = TicketEntity.create(1L, "ORDER-001", 5L, 10L, "ABC1234567",
                new BigDecimal("60000"));
        ReflectionTestUtils.setField(entity, "ticketId", 100L);
        return entity;
    }

    @Nested
    @DisplayName("findTicketEntityByTicketCode 메서드")
    class FindTicketEntityByTicketCode {

        @Test
        @DisplayName("존재하는 티켓 코드로 조회하면 TicketEntity를 반환한다")
        void returnsEntityWhenFound() {
            // given
            final TicketEntity entity = createTestEntity();
            given(ticketRepository.findByTicketCode("ABC1234567")).willReturn(Optional.of(entity));

            // when
            final TicketEntity result = ticketRetriever.findTicketEntityByTicketCode("ABC1234567");

            // then
            assertThat(result.getTicketId()).isEqualTo(100L);
            assertThat(result.getTicketCode()).isEqualTo("ABC1234567");
        }

        @Test
        @DisplayName("존재하지 않는 티켓 코드로 조회하면 TicketNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(ticketRepository.findByTicketCode("INVALID")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> ticketRetriever.findTicketEntityByTicketCode("INVALID"))
                    .isInstanceOf(TicketNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findAllTicketsByOrderIdAndUserId 메서드")
    class FindAllTicketsByOrderIdAndUserId {

        @Test
        @DisplayName("티켓이 존재하면 Ticket 리스트를 반환한다")
        void returnsTicketListWhenFound() {
            // given
            final TicketEntity entity = createTestEntity();
            given(ticketRepository.findAllByOrderIdAndUserId("ORDER-001", 1L))
                    .willReturn(List.of(entity));

            // when
            final List<Ticket> result = ticketRetriever.findAllTicketsByOrderIdAndUserId("ORDER-001", 1L);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTicketId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("빈 리스트면 TicketNotFoundException을 던진다")
        void throwsExceptionWhenEmpty() {
            // given
            given(ticketRepository.findAllByOrderIdAndUserId("ORDER-001", 1L))
                    .willReturn(Collections.emptyList());

            // when & then
            assertThatThrownBy(() -> ticketRetriever.findAllTicketsByOrderIdAndUserId("ORDER-001", 1L))
                    .isInstanceOf(TicketNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findAllTicketEntitiesById 메서드")
    class FindAllTicketEntitiesById {

        @Test
        @DisplayName("ID 목록으로 조회하면 TicketEntity 리스트를 반환한다")
        void returnsEntityListWhenFound() {
            // given
            final TicketEntity entity = createTestEntity();
            given(ticketRepository.findAllById(List.of(100L))).willReturn(List.of(entity));

            // when
            final List<TicketEntity> result = ticketRetriever.findAllTicketEntitiesById(List.of(100L));

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("빈 리스트면 TicketNotFoundException을 던진다")
        void throwsExceptionWhenEmpty() {
            // given
            given(ticketRepository.findAllById(List.of(999L))).willReturn(Collections.emptyList());

            // when & then
            assertThatThrownBy(() -> ticketRetriever.findAllTicketEntitiesById(List.of(999L)))
                    .isInstanceOf(TicketNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findAllTicketsByUserId 메서드")
    class FindAllTicketsByUserId {

        @Test
        @DisplayName("티켓이 존재하면 Ticket 리스트를 반환한다")
        void returnsTicketListWhenFound() {
            // given
            final TicketEntity entity = createTestEntity();
            given(ticketRepository.findAllByUserId(1L)).willReturn(List.of(entity));

            // when
            final List<Ticket> result = ticketRetriever.findAllTicketsByUserId(1L);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("빈 리스트면 빈 리스트를 반환한다 (예외 없음)")
        void returnsEmptyListWhenNoTickets() {
            // given
            given(ticketRepository.findAllByUserId(1L)).willReturn(Collections.emptyList());

            // when
            final List<Ticket> result = ticketRetriever.findAllTicketsByUserId(1L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findTicketByTicketCode 메서드")
    class FindTicketByTicketCode {

        @Test
        @DisplayName("존재하는 코드로 조회하면 Ticket을 반환한다")
        void returnsTicketWhenFound() {
            // given
            final TicketEntity entity = createTestEntity();
            given(ticketRepository.findByTicketCode("ABC1234567")).willReturn(Optional.of(entity));

            // when
            final Ticket result = ticketRetriever.findTicketByTicketCode("ABC1234567");

            // then
            assertThat(result.getTicketCode()).isEqualTo("ABC1234567");
        }

        @Test
        @DisplayName("존재하지 않는 코드로 조회하면 TicketNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(ticketRepository.findByTicketCode("INVALID")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> ticketRetriever.findTicketByTicketCode("INVALID"))
                    .isInstanceOf(TicketNotFoundException.class);
        }
    }
}
