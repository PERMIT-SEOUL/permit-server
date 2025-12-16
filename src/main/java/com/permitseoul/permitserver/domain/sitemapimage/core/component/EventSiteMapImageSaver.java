package com.permitseoul.permitserver.domain.sitemapimage.core.component;

import com.permitseoul.permitserver.domain.sitemapimage.core.domain.entity.EventSiteMapImageEntity;
import com.permitseoul.permitserver.domain.sitemapimage.core.repository.EventSiteMapImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventSiteMapImageSaver {
    private final EventSiteMapImageRepository eventSiteMapImageRepository;

    public void saveSiteMapImages(final List<EventSiteMapImageEntity> eventSiteMapImageEntities) {
        eventSiteMapImageRepository.saveAll(eventSiteMapImageEntities);
    }
}
