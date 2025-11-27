package com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy;

import com.permitseoul.permitserver.domain.admin.timetable.category.api.dto.NotionTimetableCategoryUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy.domain.NotionTimetableCategoryWebhookType;

public interface NotionTimetableCategoryUpdateWebhookStrategy {
    NotionTimetableCategoryWebhookType getType();

    void updateNotionTimetableStageByNotionWebhook(final NotionTimetableCategoryUpdateWebhookRequest notionTimetableCategoryUpdateWebhookRequest);
}
