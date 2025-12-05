package com.permitseoul.permitserver.domain.admin.timetable.base.core.components;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity.TimetableEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminTimetableUpdater {

    public void updateTimetable(final TimetableEntity timetableEntity,
                                final LocalDateTime timetableStartAtReq,
                                final LocalDateTime timetableEndAtReq,
                                final String notionTimetableDataSourceIdReq,
                                final String notionCategoryDataSourceIdReq,
                                final String notionStageDataSourceIdReq) {
        final LocalDateTime startAt = timetableStartAtReq == null ? timetableEntity.getStartAt() : timetableStartAtReq;
        final LocalDateTime endAt = timetableEndAtReq == null ? timetableEntity.getEndAt() : timetableEndAtReq;
        final String notionTimetableDataSourceId = notionTimetableDataSourceIdReq == null ? timetableEntity.getNotionTimetableDatasourceId() : notionTimetableDataSourceIdReq;
        final String notionCategoryDataSourceId = notionCategoryDataSourceIdReq == null ? timetableEntity.getNotionCategoryDatasourceId() : notionCategoryDataSourceIdReq;
        final String notionStageDataSourceId = notionStageDataSourceIdReq == null ? timetableEntity.getNotionStageDatasourceId() : notionStageDataSourceIdReq;
        timetableEntity.update(startAt, endAt, notionTimetableDataSourceId, notionCategoryDataSourceId, notionStageDataSourceId);
    }
}
