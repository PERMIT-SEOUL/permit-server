package com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.category.api.dto.NotionTimetableCategoryUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.category.core.component.AdminTimetableCategoryRetriever;
import com.permitseoul.permitserver.domain.admin.timetable.category.core.component.AdminTimetableCategoryUpdater;
import com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy.NotionTimetableCategoryUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy.domain.NotionTimetableCategoryWebhookType;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.global.external.notion.exception.NotFoundNotionResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotionTimetableCategoryBackgroundColorUpdateStrategyImpl implements NotionTimetableCategoryUpdateWebhookStrategy {
    private final AdminTimetableCategoryRetriever adminTimetableCategoryRetriever;
    private final AdminTimetableCategoryUpdater adminTimetableCategoryUpdater;

    private static final int NEW_BACKGROUND_COLOR_INDEX = 0;


    @Override
    public NotionTimetableCategoryWebhookType getType() {
        return NotionTimetableCategoryWebhookType.BACKGROUND_COLOR;
    }

    @Override
    public void updateNotionTimetableStageByNotionWebhook(NotionTimetableCategoryUpdateWebhookRequest webhookRequest) {
        final String rowId = webhookRequest.data().id();
        final TimetableCategoryEntity categoryEntity = adminTimetableCategoryRetriever.findTimetableCategoryEntityByTimetableCategoryRowId(rowId);

        final String newBackgroundColor = webhookRequest.data().properties().backgroundColor().richText().get(NEW_BACKGROUND_COLOR_INDEX).plainText();
        if (newBackgroundColor == null || newBackgroundColor.isEmpty()) {
            throw new NotFoundNotionResponseException();
        }

        adminTimetableCategoryUpdater.updateTimetableBackgroundColor(categoryEntity, newBackgroundColor);
    }
}
