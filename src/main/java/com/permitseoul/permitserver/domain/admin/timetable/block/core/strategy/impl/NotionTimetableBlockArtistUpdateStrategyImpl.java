package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.component.AdminTimetableBlockUpdater;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.AdminTimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotionTimetableBlockArtistUpdateStrategyImpl implements NotionTimetableBlockUpdateWebhookStrategy {
    private final AdminTimetableBlockRetriever adminTimetableBlockRetriever;
    private final AdminTimetableBlockUpdater adminTimetableBlockUpdater;

    private static final int NEW_TITLE_INDEX = 0;

    @Override
    public NotionTimetableBlockWebhookType getType() {
        return NotionTimetableBlockWebhookType.ARTIST;
    }

    @Override
    public void updateNotionTimetableBlockByNotionWebhook(final NotionTimetableBlockUpdateWebhookRequest request) {
        final String rowId = request.data().id();
        final TimetableBlockEntity blockEntity = adminTimetableBlockRetriever.findTimetableBlockEntityByNotionTimetableBlockRowId(rowId);
        final String artist = request.data()
                .properties()
                .artistActivity()
                .title()
                .get(NEW_TITLE_INDEX)
                .plainText();

        adminTimetableBlockUpdater.updateTimetableBlockArtistAndBlockName(blockEntity, artist);
    }
}
