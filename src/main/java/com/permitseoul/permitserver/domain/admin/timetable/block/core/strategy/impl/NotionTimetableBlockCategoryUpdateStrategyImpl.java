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
public class NotionTimetableBlockCategoryUpdateStrategyImpl implements NotionTimetableBlockUpdateWebhookStrategy {
    private final TimetableBlockRetriever timetableBlockRetriever;
    private final TimetableBlockUpdater timetableBlockUpdater;

    private static final int UPDATE_CATEGORY_RELATION_INDEX = 0; //노션에서 카테고리 섹션은 하나만 선택 가능하므로 0번째 인덱스 사용하면 됨

    @Override
    public NotionTimetableBlockWebhookType getType() {
        return NotionTimetableBlockWebhookType.CATEGORY;
    }

    @Override
    public void updateNotionTimetableBlockByNotionWebhook(final NotionTimetableBlockUpdateWebhookRequest request) {
        final String rowId = request.data().id();
        final TimetableBlockEntity blockEntity = timetableBlockRetriever.findTimetableBlockEntityByNotionTimetableBlockRowId(rowId);

        final String categoryRowId = request.data()
                .properties()
                .category()
                .relation()
                .get(UPDATE_CATEGORY_RELATION_INDEX)
                .id();

        timetableBlockUpdater.updateTimetableBlockCategoryRowId(blockEntity, categoryRowId);
    }
}
