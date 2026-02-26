package com.permitseoul.permitserver.domain.event.core.component;

import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.event.core.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("EventRetriever 테스트")
@ExtendWith(MockitoExtension.class)
class EventRetrieverTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventRetriever eventRetriever;

    private EventEntity createTestEntity() {
        final EventEntity entity = EventEntity.create(
                "2026 신년 콘서트", EventType.PERMIT,
                LocalDateTime.of(2026, 1, 19, 17, 0),
                LocalDateTime.of(2026, 1, 19, 21, 0),
                "서울 올림픽공원", "아티스트A", "상세 설명", 15,
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 19, 17, 0),
                "CHECK-2026");
        ReflectionTestUtils.setField(entity, "eventId", 100L);
        return entity;
    }

    @Nested
    @DisplayName("findEventById 메서드")
    class FindEventById {

        @Test
        @DisplayName("존재하면 Event를 반환한다")
        void returnsEventWhenFound() {
            // given
            given(eventRepository.findById(100L)).willReturn(Optional.of(createTestEntity()));

            // when
            final Event result = eventRetriever.findEventById(100L);

            // then
            assertThat(result.getEventId()).isEqualTo(100L);
            assertThat(result.getName()).isEqualTo("2026 신년 콘서트");
        }

        @Test
        @DisplayName("존재하지 않으면 EventNotfoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(eventRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> eventRetriever.findEventById(999L))
                    .isInstanceOf(EventNotfoundException.class);
        }
    }

    @Nested
    @DisplayName("findAllEventsById 메서드")
    class FindAllEventsById {

        @Test
        @DisplayName("ID 목록으로 조회하면 Event 리스트를 반환한다")
        void returnsEventListWhenFound() {
            // given
            given(eventRepository.findAllById(List.of(100L))).willReturn(List.of(createTestEntity()));

            // when
            final List<Event> result = eventRetriever.findAllEventsById(List.of(100L));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEventId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("빈 리스트면 빈 리스트를 반환한다")
        void returnsEmptyListWhenNoEvents() {
            // given
            given(eventRepository.findAllById(List.of(999L))).willReturn(Collections.emptyList());

            // when
            final List<Event> result = eventRetriever.findAllEventsById(List.of(999L));

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllVisibleEvents 메서드")
    class FindAllVisibleEvents {

        @Test
        @DisplayName("현재 시간으로 조회하면 보이는 이벤트 리스트를 반환한다")
        void returnsVisibleEventsWhenFound() {
            // given
            final LocalDateTime now = LocalDateTime.of(2026, 1, 10, 12, 0);
            given(eventRepository.findVisibleEvents(now)).willReturn(List.of(createTestEntity()));

            // when
            final List<Event> result = eventRetriever.findAllVisibleEvents(now);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("보이는 이벤트가 없으면 빈 리스트를 반환한다")
        void returnsEmptyListWhenNoVisibleEvents() {
            // given
            final LocalDateTime now = LocalDateTime.of(2027, 1, 1, 0, 0);
            given(eventRepository.findVisibleEvents(now)).willReturn(Collections.emptyList());

            // when
            final List<Event> result = eventRetriever.findAllVisibleEvents(now);

            // then
            assertThat(result).isEmpty();
        }
    }
}
