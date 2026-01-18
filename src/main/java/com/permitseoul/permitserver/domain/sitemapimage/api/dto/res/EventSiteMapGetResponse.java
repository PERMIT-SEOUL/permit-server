package com.permitseoul.permitserver.domain.sitemapimage.api.dto.res;


import java.util.List;

public record EventSiteMapGetResponse(
        String eventName,
        List<EventSiteMapImageInfo> siteMapImages
) {
    public static EventSiteMapGetResponse of(final String eventName, final List<EventSiteMapImageInfo> images) {
        return new EventSiteMapGetResponse(eventName, images);
    }

    public record EventSiteMapImageInfo(
            String imageUrl
    ) {
        public static EventSiteMapImageInfo of(final String imageUrl) {
            return new EventSiteMapImageInfo(imageUrl);
        }
    }
}
