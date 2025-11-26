package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.component.TimetableBlockUpdater;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.AdminTimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotionTimetableBlockRedirectUrlUpdateStrategyImpl implements NotionTimetableBlockUpdateWebhookStrategy {
    private final AdminTimetableBlockRetriever adminTimetableBlockRetriever;
    private final TimetableBlockUpdater timetableBlockUpdater;

    private static final int NEW_REDIRECT_URL_INDEX = 0;

    @Override
    public NotionTimetableBlockWebhookType getType() {
        return NotionTimetableBlockWebhookType.REDIRECT_URL;
    }

    @Override
    public void updateNotionTimetableBlockByNotionWebhook(final NotionTimetableBlockUpdateWebhookRequest request) {
        final String rowId = request.data().id();
        final TimetableBlockEntity blockEntity = adminTimetableBlockRetriever.findTimetableBlockEntityByNotionTimetableBlockRowId(rowId);
        final String redirectUrl = request.data()
                .properties()
                .redirectUrl()
                .richText()
                .get(NEW_REDIRECT_URL_INDEX)
                .plainText();

        timetableBlockUpdater.updateTimetableBlockRedirectUrl(blockEntity, redirectUrl);

    }
}
