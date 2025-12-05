package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.component.AdminTimetableBlockUpdater;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.AdminTimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotionTimetableBlockTimeUpdateStrategyImpl implements NotionTimetableBlockUpdateWebhookStrategy {
    private final AdminTimetableBlockRetriever adminTimetableBlockRetriever;
    private final AdminTimetableBlockUpdater adminTimetableBlockUpdater;

    @Override
    public NotionTimetableBlockWebhookType getType() {
        return NotionTimetableBlockWebhookType.TIME;
    }

    @Override
    public void updateNotionTimetableBlockByNotionWebhook(NotionTimetableBlockUpdateWebhookRequest request) {
        final String rowId = request.data().id(); //notion timetableBlock database rowId
        final TimetableBlockEntity blockEntity = adminTimetableBlockRetriever.findTimetableBlockEntityByNotionTimetableBlockRowId(rowId);

        final NotionTimetableBlockUpdateWebhookRequest.NotionDateValue startDateAndEndDate = request.data().properties().time().date();
        final LocalDateTime startAt = LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime(startDateAndEndDate.start());
        final LocalDateTime endAt = LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime(startDateAndEndDate.end());

        adminTimetableBlockUpdater.updateTimetableBlockTime(blockEntity, startAt, endAt);
    }
}
