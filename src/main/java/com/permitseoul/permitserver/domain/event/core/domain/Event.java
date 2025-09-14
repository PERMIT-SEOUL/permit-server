package com.permitseoul.permitserver.domain.event.core.domain;

import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Event {
    private final long eventId;
    private final String name;
    private final EventType eventType;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final String venue;
    private final String lineUp;
    private final String details;
    private final int minAge;
    private final LocalDateTime visibleStartAt;
    private final LocalDateTime visibleEndAt;
    private final String ticketCheckCode;

    public static Event fromEntity(final EventEntity entity) {
        return new Event(
                entity.getEventId(),
                entity.getName(),
                entity.getEventType(),
                entity.getStartAt(),
                entity.getEndAt(),
                entity.getVenue(),
                entity.getLineUp(),
                entity.getDetails(),
                entity.getMinAge(),
                entity.getVisibleStartAt(),
                entity.getVisibleEndAt(),
                entity.getTicketCheckCode()
        );
    }
}
