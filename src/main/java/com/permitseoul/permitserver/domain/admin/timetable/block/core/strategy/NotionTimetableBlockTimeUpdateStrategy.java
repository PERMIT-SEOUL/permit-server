package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.component.TimetableBlockUpdater;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.TimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotionTimetableBlockTimeUpdateStrategy implements NotionTimetableBlockUpdateWebhookStrategy {
    private final TimetableBlockRetriever timetableBlockRetriever;
    private final TimetableBlockUpdater timetableBlockUpdater;

    @Override
    public NotionTimetableBlockWebhookType getType() {
        return NotionTimetableBlockWebhookType.TIME;
    }

    @Override
    public void updateNotionTimetableBlockByNotionWebhook(NotionTimetableBlockUpdateWebhookRequest request) {
        final String rowId = request.data().id(); //notion timetableBlock database rowId
        final TimetableBlockEntity blockEntity = timetableBlockRetriever.findTimetableBlockEntityByNotionTimetableBlockRowId(rowId);

        final NotionTimetableBlockUpdateWebhookRequest.NotionDateValue startDateAndEndDate = request.data().properties().time().date();
        final LocalDateTime startAt = LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime(startDateAndEndDate.start());
        final LocalDateTime endAt = LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime(startDateAndEndDate.end());

        timetableBlockUpdater.updateTimetableBlockTime(blockEntity, startAt, endAt);
    }
}
