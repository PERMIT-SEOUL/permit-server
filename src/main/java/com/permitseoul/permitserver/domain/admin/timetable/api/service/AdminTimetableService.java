package com.permitseoul.permitserver.domain.admin.timetable.api.service;

import com.permitseoul.permitserver.global.external.notion.AdminNotionProvider;
import com.permitseoul.permitserver.domain.admin.timetable.core.components.AdminTimetableSaver;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;
import feign.Feign;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminTimetableService {
    private final AdminTimetableSaver adminTimetableSaver;
    private final AdminNotionProvider adminNotionProvider;

    public void saveInitialTimetableInfo(final long eventId,
                                         final LocalDateTime timetableStartAt,
                                         final LocalDateTime timetableEndAt,
                                         final String notionTimetableDataSourceId,
                                         final String notionStageDataSourceId,
                                         final String notionCategoryDataSourceId
    ) {

        try {

            //노션 stage 조회
            final NotionStageDatasourceResponse notionStageDatasource = adminNotionProvider.getNotionStageDatasource(notionTimetableDataSourceId);

            //노션 category 조회
            final NotionCategoryDatasourceResponse notionCategoryDatasource = adminNotionProvider.getNotionCategoryDatasource(notionCategoryDataSourceId);

            //노션 timetable 조회
            final NotionTimetableDatasourceResponse notionTimetableDatasource = adminNotionProvider.getNotionTimetableDatasource(notionStageDataSourceId);

            // 타임테이블 엔티티 생성
            final Timetable savedTimetable = adminTimetableSaver.saveTimetable(
                    eventId,
                    timetableStartAt,
                    timetableEndAt,
                    notionTimetableDataSourceId,
                    notionCategoryDataSourceId,
                    notionStageDataSourceId
            );

            // stage 엔티티 생성

            // category 엔티티 생성


            // timetable block 엔티티 생성

        } catch (final FeignException e) {

        }



    }
}
