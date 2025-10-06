package com.permitseoul.permitserver.domain.event.core.component;

import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.event.core.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventRetriever {
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public Event findEventById(final long eventId) {
        final EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(EventNotfoundException::new);
        return Event.fromEntity(eventEntity);
    }

    @Transactional(readOnly = true)
    public List<Event> findAllEventsById(final List<Long> eventIds) {
        final List<EventEntity> eventEntityList = eventRepository.findAllById(eventIds);
        return eventEntityList.stream()
                .map(Event::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Event> findAllVisibleEvents(final LocalDateTime now) {
        List<EventEntity> events = eventRepository.findVisibleEvents(now);
        return events.stream()
                .map(Event::fromEntity)
                .toList();
    }
}
