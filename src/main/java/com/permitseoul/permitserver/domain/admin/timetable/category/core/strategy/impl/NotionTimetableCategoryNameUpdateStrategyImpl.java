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
public class NotionTimetableCategoryNameUpdateStrategyImpl implements NotionTimetableCategoryUpdateWebhookStrategy {
    private final AdminTimetableCategoryRetriever adminTimetableCategoryRetriever;
    private final AdminTimetableCategoryUpdater adminTimetableCategoryUpdater;

    private static final int NEW_CATEGORY_NAME_INDEX = 0;

    @Override
    public NotionTimetableCategoryWebhookType getType() {
        return NotionTimetableCategoryWebhookType.NAME;
    }

    @Override
    public void updateNotionTimetableStageByNotionWebhook(final NotionTimetableCategoryUpdateWebhookRequest webhookRequest) {
        final String rowId = webhookRequest.data().id();
        final TimetableCategoryEntity categoryEntity = adminTimetableCategoryRetriever.findTimetableCategoryEntityByTimetableCategoryRowId(rowId);

        final String newCategoryName = webhookRequest.data().properties().categoryName().title().get(NEW_CATEGORY_NAME_INDEX).plainText();
        if (newCategoryName == null || newCategoryName.isEmpty()) {
            throw new NotFoundNotionResponseException();
        }

        adminTimetableCategoryUpdater.updateTimetableCategoryName(categoryEntity, newCategoryName);
    }
}
