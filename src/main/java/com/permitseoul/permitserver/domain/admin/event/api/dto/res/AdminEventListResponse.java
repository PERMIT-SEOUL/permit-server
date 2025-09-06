package com.permitseoul.permitserver.domain.admin.event.api.dto.res;

import java.util.List;

public record AdminEventListResponse(
        String eventYearMonth,
        List<AdminEventInfo> events
) {

    public static AdminEventListResponse of(final String eventYearMonth, final List<AdminEventInfo> events) {
        return new AdminEventListResponse(eventYearMonth, events);
    }

    public record AdminEventInfo(
            long eventId,
            String eventName,
            String eventVenue,
            String eventDate,
            int soldTicketCount
    ) {
        public static AdminEventInfo of(final long eventId,
                                        final String eventName,
                                        final String eventVenue,
                                        final String eventDate,
                                        final int soldTicketCount) {
            return new AdminEventInfo(eventId, eventName, eventVenue, eventDate,  soldTicketCount);
        }
    }
}
