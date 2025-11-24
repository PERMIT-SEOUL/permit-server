package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.domain.NotionTimetableBlockWebhookType;

public interface NotionTimetableBlockWebhookStrategy {
    NotionTimetableBlockWebhookType getType();

    void handle(final NotionTimetableBlockWebhookRequest request);
}
