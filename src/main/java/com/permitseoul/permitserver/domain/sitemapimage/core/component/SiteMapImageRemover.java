package com.permitseoul.permitserver.domain.sitemapimage.core.component;

import com.permitseoul.permitserver.domain.sitemapimage.core.repository.EventSiteMapImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SiteMapImageRemover {
    private final EventSiteMapImageRepository eventSiteMapImageRepository;

    public void deleteAllSiteMapImages(final long eventId) {
        eventSiteMapImageRepository.deleteAllByEventId(eventId);
    }
}
