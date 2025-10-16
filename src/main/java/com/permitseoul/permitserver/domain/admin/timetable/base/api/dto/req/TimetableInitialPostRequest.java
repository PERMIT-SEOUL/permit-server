package com.permitseoul.permitserver.domain.admin.timetable.base.api.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TimetableInitialPostRequest(
        @NotNull(message = "타임테이블 시작일은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime timetableStartAt,

        @NotNull(message = "타임테이블 종료일은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime timetableEndAt,

        @NotBlank(message = "notion_timetable_datasource_id 는 필수입니다.")
        String notionTimetableDataSourceId,

        @NotBlank(message = "notion_stage_datasource_id는 필수입니다.")
        String notionStageDataSourceId,

        @NotBlank(message = "notion_category_datasource_id는 필수입니다.")
        String notionCategoryDataSourceId
) {
}
