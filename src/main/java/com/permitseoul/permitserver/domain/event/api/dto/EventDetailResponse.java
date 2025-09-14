package com.permitseoul.permitserver.domain.event.api.dto;

import java.util.List;

public record EventDetailResponse(
        String eventName,
        String venue,
        String date,
        String time,
        int minAge,
        String details,
        List<LineupCategory> lineup,
        List<EventImageInfo> images
) {
    public static EventDetailResponse of(final String eventName,
                                         final String venue,
                                         final String date,
                                         final String time,
                                         final int minAge,
                                         final String details,
                                         final List<LineupCategory> lineup,
                                         final List<EventImageInfo> images) {
        return new EventDetailResponse(eventName, venue, date, time, minAge, details, lineup, images);
    }

    public record LineupCategory(
            String category,
            List<Artist> artists
    ) {
        public static LineupCategory of(final String category, final List<Artist> artists) {
            return new LineupCategory(category, artists);
        }
    }

    public record Artist(
            String name
    ) { }

    public record EventImageInfo(
            String imageUrl,
            int sequence
    ) {
        public static EventImageInfo of(final String imageUrl, final int sequence) {
            return new EventImageInfo(imageUrl, sequence);
        }
    }
}
