package com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy;

import com.permitseoul.permitserver.domain.admin.timetable.stage.api.dto.NotionTimetableStageUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.domain.NotionTimetableStageWebhookType;

public interface NotionTimetableStageUpdateWebhookStrategy {
    NotionTimetableStageWebhookType getType();

    void updateNotionTimetableStageByNotionWebhook(NotionTimetableStageUpdateWebhookRequest request);
}
