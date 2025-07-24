package com.permitseoul.permitserver.domain.eventImage.core.component;

import com.permitseoul.permitserver.domain.eventImage.EventImageBaseException;
import com.permitseoul.permitserver.domain.eventImage.core.domain.EventImage;
import com.permitseoul.permitserver.domain.eventImage.core.domain.entity.EventImageEntity;
import com.permitseoul.permitserver.domain.eventImage.core.exception.EventImageNotFoundException;
import com.permitseoul.permitserver.domain.eventImage.core.repository.EventImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EventImageRetriever {
    private final EventImageRepository eventImageRepository;

    @Transactional(readOnly = true)
    public EventImage findEventThumbnailImage(final long eventId) {
        final EventImageEntity eventImageEntity = eventImageRepository.findThumbnailImageEntityByEventId(eventId).orElseThrow(EventImageNotFoundException::new);
        return EventImage.fromEntity(eventImageEntity);
    }
}
