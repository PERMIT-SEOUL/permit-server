package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.component.TimetableBlockUpdater;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.TimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotionTimetableBlockArtistUpdateStrategyImpl implements NotionTimetableBlockUpdateWebhookStrategy {
    private final TimetableBlockRetriever timetableBlockRetriever;
    private final TimetableBlockUpdater timetableBlockUpdater;


    @Override
    public NotionTimetableBlockWebhookType getType() {
        return NotionTimetableBlockWebhookType.ARTIST;
    }

    @Override
    public void updateNotionTimetableBlockByNotionWebhook(final NotionTimetableBlockUpdateWebhookRequest request) {
        final String rowId = request.data().id();
        final TimetableBlockEntity blockEntity = timetableBlockRetriever.findTimetableBlockEntityByNotionTimetableBlockRowId(rowId);
        final String title = request.data()
                .properties()
                .artistActivity()
                .title()
                .get(0)
                .plainText();

        timetableBlockUpdater.updateTimetableBlockArtistAndBlockName(blockEntity, title);
    }
}
