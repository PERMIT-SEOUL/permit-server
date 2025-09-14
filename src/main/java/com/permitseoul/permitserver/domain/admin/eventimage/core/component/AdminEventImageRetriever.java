package com.permitseoul.permitserver.domain.admin.eventimage.core.component;

import com.permitseoul.permitserver.domain.eventimage.core.domain.EventImage;
import com.permitseoul.permitserver.domain.eventimage.core.domain.entity.EventImageEntity;
import com.permitseoul.permitserver.domain.eventimage.core.exception.EventImageNotFoundException;
import com.permitseoul.permitserver.domain.eventimage.core.repository.EventImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminEventImageRetriever {
    private final EventImageRepository eventImageRepository;

    @Transactional(readOnly = true)
    public List<EventImage> findAllEventImagesByEventId(final long eventId) {
        final List<EventImageEntity> eventImageEntityList = eventImageRepository.findAllByEventId(eventId);
        return eventImageEntityList.stream()
                .map(EventImage::fromEntity)
                .toList();
    }
}
