package com.permitseoul.permitserver.domain.sitemapimage.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.sitemapimage.api.dto.res.EventSiteMapGetResponse;
import com.permitseoul.permitserver.domain.sitemapimage.api.exception.SiteMapImageApiException;
import com.permitseoul.permitserver.domain.sitemapimage.core.component.SiteMapImageRetriever;
import com.permitseoul.permitserver.domain.sitemapimage.core.domain.EventSiteMapImage;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventSiteMapImageService {
    private final SiteMapImageRetriever siteMapImageRetriever;
    private final EventRetriever eventRetriever;

    @Transactional(readOnly = true)
    public EventSiteMapGetResponse getEventSiteMapImages(final long eventId) {
        try {
            final Event event = eventRetriever.findEventById(eventId);
            final List<EventSiteMapImage> eventSiteMapImages = siteMapImageRetriever.findAllEventSiteMapImagesByEventId(event.getEventId());
            if (ObjectUtils.isEmpty(eventSiteMapImages)) {
                throw new SiteMapImageApiException(ErrorCode.NOT_FOUND_SITE_MAP_IMAGE);
            }

            final List<EventSiteMapGetResponse.EventSiteMapImageInfo> siteMapImages = eventSiteMapImages.stream()
                    .sorted(Comparator.comparingInt(EventSiteMapImage::getSequence))
                    .map(eventSiteMapImage -> EventSiteMapGetResponse.EventSiteMapImageInfo.of(eventSiteMapImage.getSitemapImageUrl()))
                    .toList();

            return EventSiteMapGetResponse.of(event.getName(), siteMapImages);
        } catch (EventNotfoundException e) {
            throw new SiteMapImageApiException(ErrorCode.NOT_FOUND_EVENT);
        }
    }
}
