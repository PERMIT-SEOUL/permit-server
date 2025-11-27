package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.component.AdminTimetableBlockUpdater;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.AdminTimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotionTimetableBlockCategoryUpdateStrategyImpl implements NotionTimetableBlockUpdateWebhookStrategy {
    private final AdminTimetableBlockRetriever adminTimetableBlockRetriever;
    private final AdminTimetableBlockUpdater adminTimetableBlockUpdater;

    private static final int NEW_CATEGORY_RELATION_INDEX = 0; //노션에서 카테고리 섹션은 하나만 선택 가능하므로 0번째 인덱스 사용하면 됨

    @Override
    public NotionTimetableBlockWebhookType getType() {
        return NotionTimetableBlockWebhookType.CATEGORY;
    }

    @Override
    public void updateNotionTimetableBlockByNotionWebhook(final NotionTimetableBlockUpdateWebhookRequest request) {
        final String rowId = request.data().id();
        final TimetableBlockEntity blockEntity = adminTimetableBlockRetriever.findTimetableBlockEntityByNotionTimetableBlockRowId(rowId);

        final String categoryRowId = request.data()
                .properties()
                .category()
                .relation()
                .get(NEW_CATEGORY_RELATION_INDEX)
                .id();

        adminTimetableBlockUpdater.updateTimetableBlockCategoryRelationRowId(blockEntity, categoryRowId);
    }
}
