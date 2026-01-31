package com.permitseoul.permitserver.domain.eventimage.core.component;

import com.permitseoul.permitserver.domain.eventimage.core.domain.EventImage;
import com.permitseoul.permitserver.domain.eventimage.core.domain.entity.EventImageEntity;
import com.permitseoul.permitserver.domain.eventimage.core.exception.EventImageNotFoundException;
import com.permitseoul.permitserver.domain.eventimage.core.repository.EventImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventImageRetriever {
    private final EventImageRepository eventImageRepository;

    @Transactional(readOnly = true)
    public EventImage findEventThumbnailImage(final long eventId) {
        final EventImageEntity eventImageEntity = eventImageRepository.findThumbnailImageEntityByEventId(eventId)
                .orElseThrow(EventImageNotFoundException::new);
        return EventImage.fromEntity(eventImageEntity);
    }

    @Transactional(readOnly = true)
    public Map<Long, EventImage> findAllThumbnailsByEventIds(final List<Long> eventIds) {
        final Map<Long, EventImage> eventImageMap = eventImageRepository.findAllThumbnailsByEventIds(eventIds).stream()
                .collect(Collectors.toMap(
                        EventImageEntity::getEventId,
                        EventImage::fromEntity,
                        (a, b) -> a)
                );
        if(ObjectUtils.isEmpty(eventImageMap)) {
            throw new EventImageNotFoundException();
        }
        return eventImageMap;
    }

    @Transactional(readOnly = true)
    public List<EventImage> findAllEventImagesByEventId(final long eventId) {
        final List<EventImageEntity> eventImageEntityList = eventImageRepository.findAllByEventId(eventId);
        if (ObjectUtils.isEmpty(eventImageEntityList)) {
            throw new EventImageNotFoundException();
        }

        return eventImageEntityList.stream()
                .map(EventImage::fromEntity)
                .toList();
    }
}
