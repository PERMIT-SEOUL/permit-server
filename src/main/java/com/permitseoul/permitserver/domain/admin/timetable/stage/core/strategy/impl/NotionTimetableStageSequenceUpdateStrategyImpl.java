package com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.stage.api.dto.NotionTimetableStageUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.component.AdminTimetableStageRetriever;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.component.AdminTimetableStageUpdater;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.domain.NotionTimetableStageWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.NotionTimetableStageUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.global.external.notion.exception.NotFoundNotionResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotionTimetableStageSequenceUpdateStrategyImpl implements NotionTimetableStageUpdateWebhookStrategy {
    private final AdminTimetableStageRetriever adminTimetableStageRetriever;
    private final AdminTimetableStageUpdater adminTimetableStageUpdater;

    @Override
    public NotionTimetableStageWebhookType getType() {
        return NotionTimetableStageWebhookType.SEQUENCE;
    }

    @Override
    public void updateNotionTimetableStageByNotionWebhook(final NotionTimetableStageUpdateWebhookRequest notionTimetableStageUpdateWebhookRequest) {
        final String rowId = notionTimetableStageUpdateWebhookRequest.data().id();
        final TimetableStageEntity stageEntity = adminTimetableStageRetriever.findTimetableStageByTimetableStageRowId(rowId);

        // 노션에서 오는 숫자는 Double
        final Double number = notionTimetableStageUpdateWebhookRequest.data().properties().sequence().number();
        if (number == null) {
            throw new NotFoundNotionResponseException();
        }

        final int sequence = number.intValue();
        if (sequence < 0) {
            throw new IndexOutOfBoundsException();
        }
        adminTimetableStageUpdater.updateTimetableStageSequence(stageEntity, sequence);
    }

}
