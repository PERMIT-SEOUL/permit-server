package com.permitseoul.permitserver.domain.admin.event.api.dto.res;

import java.util.List;

public record AdminEventDetailResponse(
        long eventId,
        String eventExposureStartDate,
        String eventExposureStartTime,
        String eventExposureEndDate,
        String eventExposureEndTime,
        String verificationCode,
        String name,
        String startDate,
        String startTime,
        String endDate,
        String endTime,
        String venue,
        String lineup,
        String details,
        List<AdminEventImageInfo> images,
        int minAge
) {

    public static AdminEventDetailResponse of(final long eventId,
                                              final String eventExposureStartDate,
                                              final String eventExposureStartTime,
                                              final String eventExposureEndDate,
                                              final String eventExposureEndTime,
                                              final String verificationCode,
                                              final String name,
                                              final String startDate,
                                              final String startTime,
                                              final String endDate,
                                              final String endTime,
                                              final String venue,
                                              final String lineup,
                                              final String details,
                                              final List<AdminEventImageInfo> images,
                                              final int minAge) {
        return new AdminEventDetailResponse(
                eventId,
                eventExposureStartDate,
                eventExposureStartTime,
                eventExposureEndDate,
                eventExposureEndTime,
                verificationCode,
                name,
                startDate,
                startTime,
                endDate,
                endTime,
                venue,
                lineup,
                details,
                images,
                minAge
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
