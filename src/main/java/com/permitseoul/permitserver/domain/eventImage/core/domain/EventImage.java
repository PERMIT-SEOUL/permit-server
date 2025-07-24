package com.permitseoul.permitserver.domain.eventImage.core.domain;

import com.permitseoul.permitserver.domain.eventImage.core.domain.entity.EventImageEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventImage {
    private final long imageId;
    private final long eventId;
    private final String imageUrl;
    private final int sequence;

    public static EventImage fromEntity(final EventImageEntity eventImageEntity) {
        return new EventImage(eventImageEntity.getEventImageId(), eventImageEntity.getEventId(), eventImageEntity.getImageUrl(), eventImageEntity.getSequence());
    }
}
