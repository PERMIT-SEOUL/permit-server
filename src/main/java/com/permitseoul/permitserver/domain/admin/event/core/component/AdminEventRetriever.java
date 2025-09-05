package com.permitseoul.permitserver.domain.admin.event.core.component;

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
}
