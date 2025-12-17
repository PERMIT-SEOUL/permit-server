package com.permitseoul.permitserver.domain.sitemapimage.api.dto.res;


import java.util.List;

public record EventSiteMapGetResponse(
        List<EventSiteMapImageInfo> siteMapImages
) {
    public static EventSiteMapGetResponse of(List<EventSiteMapImageInfo> images) {
        return new EventSiteMapGetResponse(images);
    }

    public record EventSiteMapImageInfo(
            String imageUrl
    ) {
        public static EventSiteMapImageInfo of(final String imageUrl) {
            return new EventSiteMapImageInfo(imageUrl);
        }
    }
}
