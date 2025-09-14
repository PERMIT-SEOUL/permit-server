package com.permitseoul.permitserver.domain.admin.event.core.component;

import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import com.permitseoul.permitserver.domain.event.core.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminEventSaver {
    private final EventRepository eventRepository;

    public Event saveEvent(final String name,
                           final EventType eventType,
                           final LocalDateTime startDate,
                           final LocalDateTime endDate,
                           final String venue,
                           final String lineUp,
                           final String details,
                           final int minAge,
                           final LocalDateTime visibleStartDate,
                           final LocalDateTime visibleEndDate,
                           final String ticketCheckCode) {
        return Event.fromEntity(
                eventRepository.save(EventEntity.create(name, eventType, startDate, endDate, venue, lineUp, details, minAge, visibleStartDate, visibleEndDate, ticketCheckCode))
        );
    }
}
