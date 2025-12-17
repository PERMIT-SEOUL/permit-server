package com.permitseoul.permitserver.domain.admin.event.api.dto.res;

import com.permitseoul.permitserver.domain.event.core.domain.EventType;

import java.util.List;

public record AdminEventDetailResponse(
        long eventId,
        String eventExposureStartDate,
        String eventExposureStartTime,
        String eventExposureEndDate,
        String eventExposureEndTime,
        String verificationCode,
        String name,
        EventType eventType,
        String startDate,
        String startTime,
        String endDate,
        String endTime,
        String venue,
        String lineup,
        String details,
        List<AdminEventImageInfo> images,
        int minAge,
        List<AdminEventImageInfo> siteMapImages
) {

    public static AdminEventDetailResponse of(final long eventId,
                                              final String eventExposureStartDate,
                                              final String eventExposureStartTime,
                                              final String eventExposureEndDate,
                                              final String eventExposureEndTime,
                                              final String verificationCode,
                                              final String name,
                                              final EventType eventType,
                                              final String startDate,
                                              final String startTime,
                                              final String endDate,
                                              final String endTime,
                                              final String venue,
                                              final String lineup,
                                              final String details,
                                              final List<AdminEventImageInfo> images,
                                              final int minAge,
                                              final List<AdminEventImageInfo> siteMapImages) {
        return new AdminEventDetailResponse(
                eventId,
                eventExposureStartDate,
                eventExposureStartTime,
                eventExposureEndDate,
                eventExposureEndTime,
                verificationCode,
                name,
                eventType,
                startDate,
                startTime,
                endDate,
                endTime,
                venue,
                lineup,
                details,
                images,
                minAge,
                siteMapImages
        );
    }

    public record AdminEventImageInfo(
            String imageUrl
    ) {
        public static AdminEventImageInfo of(final String imageUrl) {
            return new AdminEventImageInfo(imageUrl);
        }
    }
}
