package com.permitseoul.permitserver.domain.admin.event.core.component;

import com.permitseoul.permitserver.domain.admin.event.core.exception.AdminEventNotFoundException;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import com.permitseoul.permitserver.domain.event.core.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminEventRetriever {
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        final List<EventEntity> eventEntities = eventRepository.findAll();
        return eventEntities.stream()
                .map(Event::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Event findEventById(final long eventId) {
        final EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(AdminEventNotFoundException::new);
        return Event.fromEntity(eventEntity);
    }

    @Transactional(readOnly = true)
    public EventEntity findEventEntityById(final long eventId) {
        return eventRepository.findById(eventId).orElseThrow(AdminEventNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public void validateEventExist(final long eventId) {
        eventRepository.findById(eventId).orElseThrow(AdminEventNotFoundException::new);
    }
}
