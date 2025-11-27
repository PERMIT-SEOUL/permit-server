package com.permitseoul.permitserver.domain.admin.timetable.base.api.service;

import com.permitseoul.permitserver.domain.admin.timetable.block.core.component.AdminTimetableBlockSaver;
import com.permitseoul.permitserver.domain.admin.timetable.blockmedia.core.component.AdminTimetableBlockMediaSaver;
import com.permitseoul.permitserver.domain.admin.timetable.category.core.component.AdminTimetableCategorySaver;
import com.permitseoul.permitserver.domain.admin.timetable.base.core.components.AdminTimetableSaver;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.component.AdminTimetableStageSaver;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity.TimetableBlockMediaEntity;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.permitseoul.permitserver.domain.admin.util.NotionResponseMapper.*;

@Component
@RequiredArgsConstructor
public class AdminTimetableFacade {
    private final AdminTimetableSaver adminTimetableSaver;
    private final AdminTimetableStageSaver adminTimetableStageSaver;
    private final AdminTimetableCategorySaver adminTimetableCategorySaver;
    private final AdminTimetableBlockSaver adminTimetableBlockSaver;
    private final AdminTimetableBlockMediaSaver adminTimetableBlockMediaSaver;

    @Transactional
    public void saveInitialTimetableInfos(final long eventId,
                                          final LocalDateTime timetableStartAt,
                                          final LocalDateTime timetableEndAt,
                                          final String notionTimetableDataSourceId,
                                          final String notionStageDataSourceId,
                                          final String notionCategoryDataSourceId,
                                          final NotionTimetableDatasourceResponse notionTimetableDatasourceResponse,
                                          final NotionStageDatasourceResponse notionStageDatasourceResponse,
                                          final NotionCategoryDatasourceResponse notionCategoryDatasourceResponse) {

        final Timetable savedTimetable = adminTimetableSaver.saveTimetable(
                eventId,
                timetableStartAt,
                timetableEndAt,
                notionTimetableDataSourceId,
                notionStageDataSourceId,
                notionCategoryDataSourceId
        );
        final long savedTimetableId = savedTimetable.getTimetableId();

        final List<TimetableStageEntity> timetableStageEntities = mapToTimetableStageEntities(savedTimetableId, notionStageDatasourceResponse);
        adminTimetableStageSaver.saveAllTimetableStagesEntities(timetableStageEntities);

        final List<TimetableCategoryEntity> timetableCategoryEntities = mapToTimetableCategoryEntities(savedTimetableId, notionCategoryDatasourceResponse);
        adminTimetableCategorySaver.saveAllTimetableCategoryEntities(timetableCategoryEntities);

        final List<TimetableBlockEntity> blockEntities = mapToTimetableBlockEntities(savedTimetableId, notionTimetableDatasourceResponse);
        List<TimetableBlock> savedBlocks = adminTimetableBlockSaver.saveAllTimetableBlocks(blockEntities);

        final List<TimetableBlockMediaEntity> blockMediaEntities = mapToTimetableBlockMediaEntities(savedBlocks, notionTimetableDatasourceResponse);
        adminTimetableBlockMediaSaver.saveAllBlockMedia(blockMediaEntities);
    }
}
