package com.permitseoul.permitserver.domain.sitemapimage.core.domain;


import com.permitseoul.permitserver.domain.sitemapimage.core.domain.entity.EventSiteMapImageEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventSiteMapImage {

    private final long siteMapImagesId;
    private final int sequence;
    private final String sitemapImageUrl;
    private final long eventId;

    public static EventSiteMapImage fromEntity(final EventSiteMapImageEntity entity) {
        return new EventSiteMapImage(
                entity.getSiteMapImagesId(),
                entity.getSequence(),
                entity.getSitemapImageUrl(),
                entity.getEventId()
        );
    }
}