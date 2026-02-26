package com.permitseoul.permitserver.domain.sitemapimage.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.sitemapimage.api.dto.res.EventSiteMapGetResponse;
import com.permitseoul.permitserver.domain.sitemapimage.api.exception.SiteMapImageApiException;
import com.permitseoul.permitserver.domain.sitemapimage.core.component.SiteMapImageRetriever;
import com.permitseoul.permitserver.domain.sitemapimage.core.domain.EventSiteMapImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventSiteMapImageService 테스트")
class EventSiteMapImageServiceTest {

    @Mock
    private SiteMapImageRetriever siteMapImageRetriever;
    @Mock
    private EventRetriever eventRetriever;
    @InjectMocks
    private EventSiteMapImageService eventSiteMapImageService;

    private static final long EVENT_ID = 100L;
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 2, 13, 14, 0);

    private Event createEvent() {
        return new Event(EVENT_ID, "테스트 이벤트", EventType.PERMIT, NOW.minusDays(1), NOW.plusDays(1),
                "서울", "라인업", "상세", 0, NOW.minusDays(7), NOW.plusDays(7), "CHECK-CODE");
    }

    @Test
    @DisplayName("정상: 사이트맵 이미지 목록 조회")
    void success() {
        final Event event = createEvent();
        final List<EventSiteMapImage> images = List.of(
                new EventSiteMapImage(1L, 1, "https://example.com/map1.png", EVENT_ID),
                new EventSiteMapImage(2L, 2, "https://example.com/map2.png", EVENT_ID));
        when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
        when(siteMapImageRetriever.findAllEventSiteMapImagesByEventId(EVENT_ID)).thenReturn(images);

        final EventSiteMapGetResponse result = eventSiteMapImageService.getEventSiteMapImages(EVENT_ID);

        assertThat(result.eventName()).isEqualTo("테스트 이벤트");
        assertThat(result.siteMapImages()).hasSize(2);
        assertThat(result.siteMapImages().get(0).imageUrl()).isEqualTo("https://example.com/map1.png");
    }

    @Test
    @DisplayName("예외: 이벤트 미존재 → SiteMapImageApiException")
    void throwsWhenEventNotFound() {
        when(eventRetriever.findEventById(EVENT_ID)).thenThrow(new EventNotfoundException());

        assertThatThrownBy(() -> eventSiteMapImageService.getEventSiteMapImages(EVENT_ID))
                .isInstanceOf(SiteMapImageApiException.class);
    }

    @Test
    @DisplayName("예외: 사이트맵 이미지 없음 → SiteMapImageApiException")
    void throwsWhenNoImages() {
        final Event event = createEvent();
        when(eventRetriever.findEventById(EVENT_ID)).thenReturn(event);
        when(siteMapImageRetriever.findAllEventSiteMapImagesByEventId(EVENT_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> eventSiteMapImageService.getEventSiteMapImages(EVENT_ID))
                .isInstanceOf(SiteMapImageApiException.class);
    }
}
