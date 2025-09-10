package com.permitseoul.permitserver.domain.admin.event.core.component;

import com.permitseoul.permitserver.domain.admin.event.api.dto.req.AdminEventUpdateRequest;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class AdminEventUpdater {

    public void updateEvent(final EventEntity eventEntity,
                            final AdminEventUpdateRequest eventUpdateRequest,
                            final LocalDateTime visibleStartAt,
                            final LocalDateTime visibleEndAt,
                            final LocalDateTime startAt,
                            final LocalDateTime endAt) {

        final String eventName = eventUpdateRequest.name() == null ? eventEntity.getName() : eventUpdateRequest.name();
        final EventType eventType = eventUpdateRequest.eventType() == null ? eventEntity.getEventType() : eventUpdateRequest.eventType();
        final String venue = eventUpdateRequest.venue() == null ? eventEntity.getVenue() : eventUpdateRequest.venue();
        final String lineUp = eventUpdateRequest.lineup() == null ? eventEntity.getLineUp() : eventUpdateRequest.lineup();
        final String details = eventUpdateRequest.details() == null ? eventEntity.getDetails() : eventUpdateRequest.details();
        final int minAge = eventUpdateRequest.minAge() == null ? eventEntity.getMinAge() : eventUpdateRequest.minAge();
        final String verificationCode = eventUpdateRequest.verificationCode() == null ? eventEntity.getTicketCheckCode() : eventUpdateRequest.verificationCode();

        eventEntity.updateEvent(eventName, eventType, startAt, endAt, venue, lineUp, details, minAge, visibleStartAt, visibleEndAt, verificationCode);
    }
}
