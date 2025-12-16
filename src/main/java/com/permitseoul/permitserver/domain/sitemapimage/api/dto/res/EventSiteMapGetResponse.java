package com.permitseoul.permitserver.domain.sitemapimage.api.dto.res;


import java.util.List;

public record EventSiteMapGetResponse(
        List<EventSiteMapImageInfo> images
) {
    public static EventSiteMapGetResponse of(List<EventSiteMapImageInfo> images) {
        return new EventSiteMapGetResponse(images);
    }

    public record EventSiteMapImageInfo(
            String imageUrl,
            int sequence
    ) {
        public static EventSiteMapImageInfo of(final String imageUrl,
                                               final int sequence) {
            return new EventSiteMapImageInfo(imageUrl, sequence);
        }
    }
}
