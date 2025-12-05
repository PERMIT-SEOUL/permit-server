package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.domain.NotionTimetableBlockWebhookType;

public interface NotionTimetableBlockUpdateWebhookStrategy {
    NotionTimetableBlockWebhookType getType();
    void updateNotionTimetableBlockByNotionWebhook(final NotionTimetableBlockUpdateWebhookRequest request);
}
