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
public class NotionTimetableCategoryLineColorUpdateStrategyImpl implements NotionTimetableCategoryUpdateWebhookStrategy {
    private final AdminTimetableCategoryRetriever adminTimetableCategoryRetriever;
    private final AdminTimetableCategoryUpdater adminTimetableCategoryUpdater;

    private static final int NEW_LINE_COLOR_INDEX = 0;

    @Override
    public NotionTimetableCategoryWebhookType getType() {
        return NotionTimetableCategoryWebhookType.LINE_COLOR;
    }

    @Override
    public void updateNotionTimetableCategoryByNotionWebhook(final NotionTimetableCategoryUpdateWebhookRequest webhookRequest) {
        final String rowId = webhookRequest.data().id();
        final TimetableCategoryEntity categoryEntity = adminTimetableCategoryRetriever.findTimetableCategoryEntityByTimetableCategoryRowId(rowId);

        final NotionTimetableCategoryUpdateWebhookRequest.NotionRichTextProperty lineColorProperty = webhookRequest.data().properties().lineColor();
        if (lineColorProperty == null || lineColorProperty.richText() == null || lineColorProperty.richText().isEmpty()) {
            throw new NotFoundNotionResponseException();
        }

        final var richTextList = lineColorProperty.richText();
        final String newLineColor = richTextList.get(NEW_LINE_COLOR_INDEX).plainText();
        if (newLineColor == null || newLineColor.isBlank()) {
            throw new NotFoundNotionResponseException();
        }

        adminTimetableCategoryUpdater.updateTimetableLineColor(categoryEntity, newLineColor);
    }
}
