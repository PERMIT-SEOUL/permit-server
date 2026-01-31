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
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.SecureUrlUtil;
import com.permitseoul.permitserver.global.util.TimeFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRetriever eventRetriever;
    private final EventImageRetriever eventImageRetriever;
    private final SecureUrlUtil secureUrlUtil;

    @Transactional(readOnly = true)
    public EventAllResponse getAllVisibleEvents() {
        try {
            final LocalDateTime now = LocalDateTime.now();
            final List<Event> eventList = eventRetriever.findAllVisibleEvents(now);
            if (ObjectUtils.isEmpty(eventList)) {
                return new EventAllResponse(List.of(), List.of(), List.of());
            }
            
            final List<Long> eventIds = eventList.stream()
                    .map(Event::getEventId)
                    .toList();
            final Map<Long, EventImage> thumbnailMap = eventImageRetriever.findAllThumbnailsByEventIds(eventIds);

            return new EventAllResponse(
                    filteringEventByEventType(eventList, EventType.PERMIT, thumbnailMap),
                    filteringEventByEventType(eventList, EventType.CEILING, thumbnailMap),
                    filteringEventByEventType(eventList, EventType.OLYMPAN, thumbnailMap));
        } catch (EventImageNotFoundException e) {
            throw new NotFoundEventException(ErrorCode.NOT_FOUND_EVENT_IMAGE);
        }

    }

    @Transactional(readOnly = true)
    public EventDetailResponse getEventDetail(final long eventId) {
        try {
            final Event event = eventRetriever.findEventById(eventId);

            final String eventDate = LocalDateTimeFormatterUtil.formatStartEndDate(event.getStartAt(), event.getEndAt());
            final String eventTime = TimeFormatterUtil.formatEventTime(event.getStartAt(), event.getEndAt());

            final List<EventImage> eventImageList = eventImageRetriever.findAllEventImagesByEventId(event.getEventId());
            final List<EventDetailResponse.EventImageInfo> imagesInfo = eventImageList.stream()
                    .sorted(Comparator.comparingInt(EventImage::getSequence))
                    .map(eventImage -> EventDetailResponse.EventImageInfo.of(eventImage.getImageUrl(), eventImage.getSequence()))
                    .toList();

            return EventDetailResponse.of(
                    event.getName(),
                    event.getVenue(),
                    eventDate, eventTime,
                    event.getMinAge(),
                    event.getDetails(),
                    parseLineup(event.getLineUp()),
                    imagesInfo
            );
        } catch (EventNotfoundException e) {
            throw new NotFoundEventException(ErrorCode.NOT_FOUND_EVENT);
        } catch (EventImageNotFoundException e) {
            throw new NotFoundEventException(ErrorCode.NOT_FOUND_EVENT_IMAGE);
        }
    }

    private List<EventDetailResponse.LineupCategory> parseLineup(final String rawLineupText) {
        final List<EventDetailResponse.LineupCategory> result = new ArrayList<>();
        if (rawLineupText == null || rawLineupText.isBlank()) return result;

        final String[] lines = rawLineupText.split("\\r?\\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isBlank()) continue;

            final int closingBracketIndex = line.indexOf(']');
            if (!line.startsWith("[") || closingBracketIndex == -1) continue;

            final String category = line.substring(0, closingBracketIndex + 1);

            final String artistPart = line.substring(closingBracketIndex + 1).trim();
            if (artistPart.isBlank()) continue;

            final List<EventDetailResponse.Artist> artists = Arrays.stream(
                    artistPart.split("\\s*,\\s*")) // 쉼표 기준// split, 주변 // 공백 무시
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(EventDetailResponse.Artist::new)
                    .toList();

            if (!artists.isEmpty()) {
                result.add(EventDetailResponse.LineupCategory.of(category, artists));
            }
        }

        return result;
    }

    private List<EventAllResponse.EventInfo> filteringEventByEventType(final List<Event> eventList,
                                                                       final EventType type,
                                                                       final Map<Long, EventImage> thumbnailMap) {
        if (ObjectUtils.isEmpty(eventList)) {
            return List.of();
        }
        return eventList.stream()
                .filter(event -> event.getEventType() == type)
                .map(event -> {
                    final EventImage thumbnailImage = thumbnailMap.get(event.getEventId());
                    final String thumbnailUrl = (thumbnailImage != null) ? thumbnailImage.getImageUrl() : null;

                    if (ObjectUtils.isEmpty(thumbnailUrl)) {
                        throw new NotFoundEventException(ErrorCode.NOT_FOUND_EVENT_IMAGE);
                    }

                    return EventAllResponse.EventInfo.of(
                            secureUrlUtil.encode(event.getEventId()),
                            event.getName(),
                            thumbnailUrl);
                })
                .toList();
    }
}
