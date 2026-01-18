package com.permitseoul.permitserver.domain.sitemapimage.core.component;

import com.permitseoul.permitserver.domain.sitemapimage.core.domain.EventSiteMapImage;
import com.permitseoul.permitserver.domain.sitemapimage.core.domain.entity.EventSiteMapImageEntity;
import com.permitseoul.permitserver.domain.sitemapimage.core.repository.EventSiteMapImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SiteMapImageRetriever {
    private final EventSiteMapImageRepository eventSiteMapImageRepository;

    public List<EventSiteMapImage> findAllEventSiteMapImagesByEventId(final long eventId) {
        final List<EventSiteMapImageEntity> siteMapImageEntities = Optional.ofNullable(
                eventSiteMapImageRepository.findAllByEventId(eventId))
                .orElseGet(List::of);

        return siteMapImageEntities.stream()
                .map(EventSiteMapImage::fromEntity)
                .toList();
    }
}
