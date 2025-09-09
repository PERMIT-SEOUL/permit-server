package com.permitseoul.permitserver.domain.admin.eventimage.core.component;

import com.permitseoul.permitserver.domain.eventimage.core.domain.entity.EventImageEntity;
import com.permitseoul.permitserver.domain.eventimage.core.repository.EventImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminEventImageSaver {
    private final EventImageRepository eventImageRepository;

    public void saveEventImages(final List<EventImageEntity> eventImages) {
        eventImageRepository.saveAll(eventImages);
    }
}
