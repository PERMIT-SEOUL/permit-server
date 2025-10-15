package com.permitseoul.permitserver.domain.admin.timetable.api.service;

import com.permitseoul.permitserver.domain.admin.timetable.category.core.component.AdminTimetableCategorySaver;
import com.permitseoul.permitserver.domain.admin.timetable.core.components.AdminTimetableSaver;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.AdminTimetableStageSaver;
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

import static com.permitseoul.permitserver.domain.admin.util.NotionResponseMapper.mapToTimetableCategoryEntities;
import static com.permitseoul.permitserver.domain.admin.util.NotionResponseMapper.mapToTimetableStageEntities;

@Component
@RequiredArgsConstructor
public class AdminTimetableFacade {
    private final AdminTimetableSaver adminTimetableSaver;
    private final AdminTimetableStageSaver adminTimetableStageSaver;
    private final AdminTimetableCategorySaver adminTimetableCategorySaver;

    @Transactional
    public void saveInitialTimetableInfos(final long eventId,
                                          final LocalDateTime timetableStartAt,
                                          final LocalDateTime timetableEndAt,
                                          final String notionTimetableDataSourceId,
                                          final String notionStageDataSourceId,
                                          final String notionCategoryDataSourceId,
                                          final NotionStageDatasourceResponse notionStageDatasourceResponse,
                                          final NotionCategoryDatasourceResponse notionCategoryDatasourceResponse,
                                          final NotionTimetableDatasourceResponse notionTimetableDatasourceResponse) {

        final Timetable savedTimetable = adminTimetableSaver.saveTimetable(
                eventId,
                timetableStartAt,
                timetableEndAt,
                notionTimetableDataSourceId,
                notionCategoryDataSourceId,
                notionStageDataSourceId
        );
        final long savedTimetableId = savedTimetable.getTimetableId();

        final List<TimetableStageEntity> timetableStageEntities = mapToTimetableStageEntities(savedTimetableId, notionStageDatasourceResponse);
        adminTimetableStageSaver.saveAllTimetableStages(timetableStageEntities);

        final List<TimetableCategoryEntity> timetableCategoryEntities = mapToTimetableCategoryEntities(savedTimetableId, notionCategoryDatasourceResponse);
        adminTimetableCategorySaver.saveAllTimetableCategoryEntities(timetableCategoryEntities);

        // timetable block 엔티티 생성


    }



}
