package com.permitseoul.permitserver.domain.event.api.service;

import com.permitseoul.permitserver.domain.event.api.dto.EventAllResponse;
import com.permitseoul.permitserver.domain.event.api.exception.NotFoundEventException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.eventImage.core.component.EventImageRetriever;
import com.permitseoul.permitserver.domain.eventImage.core.domain.EventImage;
import com.permitseoul.permitserver.domain.eventImage.core.exception.EventImageNotFoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.SecureUrlUtil;
import io.netty.util.internal.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRetriever eventRetriever;
    private final EventImageRetriever eventImageRetriever;

    public EventAllResponse getAllVisibleEvents() {
        final LocalDateTime now = LocalDateTime.now();
        final List<Event> eventList = eventRetriever.findAllVisibleEvents(now);

        return new EventAllResponse(
                filteringEventByEventType(eventList, EventType.PERMIT),
                filteringEventByEventType(eventList, EventType.CEILING),
                filteringEventByEventType(eventList, EventType.FESTIVAL)
        );
    }

    private List<EventAllResponse.EventInfo> filteringEventByEventType(final List<Event> eventList, final EventType type) {
        try {
            if (ObjectUtils.isEmpty(eventList)) {
                return List.of();
            }
            return eventList.stream()
                    .filter(event -> event.getEventType() == type)
                    .map(event -> {
                        final EventImage thumbnailImage = eventImageRetriever.findEventThumbnailImage(event.getEventId());
                        return EventAllResponse.EventInfo.of(
                                SecureUrlUtil.encodeUrl(event.getEventId()),
                                event.getName(),
                                thumbnailImage.getImageUrl()
                        );
                    })
                    .toList();
        } catch (EventImageNotFoundException e) {
            throw new NotFoundEventException(ErrorCode.NOT_FOUND_EVENT_IMAGE);
        }
    }
}
