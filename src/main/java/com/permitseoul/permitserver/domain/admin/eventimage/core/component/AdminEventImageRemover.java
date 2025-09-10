package com.permitseoul.permitserver.domain.admin.eventimage.core.component;

import com.permitseoul.permitserver.domain.eventimage.core.repository.EventImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminEventImageRemover {
    private final EventImageRepository eventImageRepository;

    public void deleteAllByEventId(final long eventId) {
        eventImageRepository.deleteAllByEventId(eventId);
    }
}
