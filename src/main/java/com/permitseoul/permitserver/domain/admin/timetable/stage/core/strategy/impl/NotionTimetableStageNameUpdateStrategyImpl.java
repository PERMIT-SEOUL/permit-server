package com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.stage.api.dto.NotionTimetableStageUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.component.AdminTimetableStageRetriever;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.component.AdminTimetableStageUpdater;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.domain.NotionTimetableStageWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.NotionTimetableStageUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.global.external.notion.exception.NotFoundNotionResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotionTimetableStageNameUpdateStrategyImpl implements NotionTimetableStageUpdateWebhookStrategy {
    private final AdminTimetableStageRetriever adminTimetableStageRetriever;
    private final AdminTimetableStageUpdater adminTimetableStageUpdater;

    private static final int NEW_STAGE_NAME_INDEX = 0;

    @Override
    public NotionTimetableStageWebhookType getType() {
        return NotionTimetableStageWebhookType.NAME;
    }

    @Override
    public void updateNotionTimetableStageByNotionWebhook(final NotionTimetableStageUpdateWebhookRequest request) {
        final String rowId = request.data().id();
        final TimetableStageEntity stageEntity = adminTimetableStageRetriever.findTimetableStageByTimetableStageRowId(rowId);

        final NotionTimetableStageUpdateWebhookRequest.NotionTitleProperty stageNameProperty = request.data().properties().stageName();
        if (stageNameProperty == null || stageNameProperty.title() == null || stageNameProperty.title().isEmpty()) {
            throw new NotFoundNotionResponseException();
        }

        final String newStageName = stageNameProperty.title().get(NEW_STAGE_NAME_INDEX).plainText();
        if (newStageName == null || newStageName.isBlank()) {
            throw new NotFoundNotionResponseException();
        }
        adminTimetableStageUpdater.updateTimetableStageName(stageEntity, newStageName);
    }
}
