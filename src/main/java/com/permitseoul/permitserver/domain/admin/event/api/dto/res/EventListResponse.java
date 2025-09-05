package com.permitseoul.permitserver.domain.admin.event.api.dto.res;

import java.util.List;

public record EventListResponse(
        String eventYearMonth,
        List<EventInfo> events
) {
    public record EventInfo(
            long eventId,
            String eventName,
            String eventVenue,
            String eventDate,
            int soldTicketCount
    ) {

    }
}
