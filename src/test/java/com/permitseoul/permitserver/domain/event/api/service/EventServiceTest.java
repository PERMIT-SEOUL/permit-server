package com.permitseoul.permitserver.domain.event.api.service;

import com.permitseoul.permitserver.domain.event.api.dto.EventAllResponse;
import com.permitseoul.permitserver.domain.event.api.dto.EventDetailResponse;
import com.permitseoul.permitserver.domain.event.api.exception.NotFoundEventException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.eventimage.core.component.EventImageRetriever;
import com.permitseoul.permitserver.domain.eventimage.core.domain.EventImage;
import com.permitseoul.permitserver.domain.eventimage.core.exception.EventImageNotFoundException;
import com.permitseoul.permitserver.global.util.SecureUrlUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService 테스트")
class EventServiceTest {

    @Mock
    private EventRetriever eventRetriever;
    @Mock
    private EventImageRetriever eventImageRetriever;
    @Mock
    private SecureUrlUtil secureUrlUtil;
    @InjectMocks
    private EventService eventService;

    private static final long EVENT_ID = 100L;
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 2, 18, 14, 0);

    private Event createEvent(long id, String name, EventType type) {
        return new Event(id, name, type, NOW.minusDays(1), NOW.plusDays(1),
                "서울", "[POP] 아티스트A, 아티스트B", "상세", 0, NOW.minusDays(7), NOW.plusDays(7), "CHECK-CODE");
    }

    @Nested
    @DisplayName("getAllVisibleEvents")
    class GetAllVisibleEventsTest {

        @Test
        @DisplayName("정상: 이벤트 목록 조회 → 타입별 분류")
        void success() {
            final List<Event> eventList = List.of(
                    createEvent(1L, "퍼밋 이벤트", EventType.PERMIT),
                    createEvent(2L, "천장 이벤트", EventType.CEILING));
            final Map<Long, EventImage> thumbnailMap = Map.of(
                    1L, new EventImage(10L, 1L, "https://img.com/1.png", 1),
                    2L, new EventImage(11L, 2L, "https://img.com/2.png", 1));
            when(eventRetriever.findAllVisibleEvents(any(LocalDateTime.class))).thenReturn(eventList);
            when(eventImageRetriever.findAllThumbnailsByEventIds(anyList())).thenReturn(thumbnailMap);
            when(secureUrlUtil.encode(1L)).thenReturn("encoded-1");
            when(secureUrlUtil.encode(2L)).thenReturn("encoded-2");

            final EventAllResponse result = eventService.getAllVisibleEvents();

            assertThat(result.permit()).hasSize(1);
            assertThat(result.permit().get(0).eventName()).isEqualTo("퍼밋 이벤트");
            assertThat(result.ceilingService()).hasSize(1);
            assertThat(result.ceilingService().get(0).eventName()).isEqualTo("천장 이벤트");
            assertThat(result.festival()).isEmpty();
        }

        @Test
        @DisplayName("정상: 이벤트 빈 목록 → 빈 응답")
        void emptyList() {
            when(eventRetriever.findAllVisibleEvents(any(LocalDateTime.class))).thenReturn(List.of());

            final EventAllResponse result = eventService.getAllVisibleEvents();

            assertThat(result.permit()).isEmpty();
            assertThat(result.ceilingService()).isEmpty();
            assertThat(result.festival()).isEmpty();
        }

        @Test
        @DisplayName("예외: 썸네일 이미지 미존재 → NotFoundEventException")
        void throwsWhenImageNotFound() {
            final List<Event> eventList = List.of(createEvent(1L, "이벤트", EventType.PERMIT));
            when(eventRetriever.findAllVisibleEvents(any(LocalDateTime.class))).thenReturn(eventList);
            when(eventImageRetriever.findAllThumbnailsByEventIds(anyList()))
                    .thenThrow(new EventImageNotFoundException());

            assertThatThrownBy(() -> eventService.getAllVisibleEvents())
                    .isInstanceOf(NotFoundEventException.class);
        }
    }

    @Nested
    @DisplayName("getEventDetail")
    class GetEventDetailTest {

        @Test
        @DisplayName("정상: 이벤트 상세 조회 → 라인업 파싱 포함")
        void success() {
            final Event event = createEvent(EVENT_ID, "테스트 이벤트", EventType.PERMIT);
            final List<EventImage> images = List.of(
                    new EventImage(10L, EVENT_ID, "https://img.com/detail1.png", 1),
                    new EventImage(11L, EVENT_ID, "https://img.com/detail2.png", 2));
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(eventImageRetriever.findAllEventImagesByEventId(EVENT_ID)).thenReturn(images);

            final EventDetailResponse result = eventService.getEventDetail(EVENT_ID);

            assertThat(result.eventName()).isEqualTo("테스트 이벤트");
            assertThat(result.venue()).isEqualTo("서울");
            assertThat(result.minAge()).isZero();
            assertThat(result.details()).isEqualTo("상세");
            assertThat(result.images()).hasSize(2);
            // 라인업 파싱 검증
            assertThat(result.lineup()).hasSize(1);
            assertThat(result.lineup().get(0).category()).isEqualTo("[POP]");
            assertThat(result.lineup().get(0).artists()).hasSize(2);
        }

        @Test
        @DisplayName("예외: 이벤트 미존재 → NotFoundEventException")
        void throwsWhenEventNotFound() {
            when(eventRetriever.findEventById(EVENT_ID)).thenThrow(new EventNotfoundException());

            assertThatThrownBy(() -> eventService.getEventDetail(EVENT_ID))
                    .isInstanceOf(NotFoundEventException.class);
        }

        @Test
        @DisplayName("예외: 이벤트 이미지 미존재 → NotFoundEventException")
        void throwsWhenImageNotFound() {
            final Event event = createEvent(EVENT_ID, "테스트 이벤트", EventType.PERMIT);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
            when(eventImageRetriever.findAllEventImagesByEventId(EVENT_ID))
                    .thenThrow(new EventImageNotFoundException());

            assertThatThrownBy(() -> eventService.getEventDetail(EVENT_ID))
                    .isInstanceOf(NotFoundEventException.class);
        }
    }
}
