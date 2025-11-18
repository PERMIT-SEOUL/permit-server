package com.permitseoul.permitserver.domain.admin.timetable.base.api.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TimetableUpdateRequest(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime timetableStartAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime timetableEndAt,
        String notionTimetableDataSourceId,
        String notionStageDataSourceId,
        String notionCategoryDataSourceId
) {
}
