package com.permitseoul.permitserver.domain.event.api.dto;

import java.util.List;

public record EventAllResponse(
        List<EventInfo> permit,
        List<EventInfo> ceilingService,
        List<EventInfo> festival
) {

    public static EventAllResponse create(final List<EventInfo> permit,
                                          final List<EventInfo> ceilingService,
                                          final List<EventInfo> festival) {
        return new EventAllResponse(permit, ceilingService, festival);
    }

    public record EventInfo(
        String eventId,
        String eventName,
        String thumbnailImageUrl) {
        public static EventInfo of(final String eventId, final String eventName, final String thumbnailImageUrl) {
            return new EventInfo(eventId, eventName, thumbnailImageUrl);
        }
    }
}


