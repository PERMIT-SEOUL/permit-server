package com.permitseoul.permitserver.domain.admin.timetable.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.TimetableStage;
import com.permitseoul.permitserver.global.exception.PermitIllegalStateException;
import com.permitseoul.permitserver.global.external.notion.NotionProvider;
import com.permitseoul.permitserver.domain.admin.timetable.core.components.AdminTimetableSaver;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.global.external.notion.NotionRelationValidator;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminTimetableService {
    private final AdminTimetableSaver adminTimetableSaver;
    private final NotionProvider notionProvider;

    public void saveInitialTimetableInfo(final long eventId,
                                         final LocalDateTime timetableStartAt,
                                         final LocalDateTime timetableEndAt,
                                         final String notionTimetableDataSourceId,
                                         final String notionStageDataSourceId,
                                         final String notionCategoryDataSourceId
    ) {

        try {

            //노션 stage 조회
            final NotionStageDatasourceResponse notionStageDatasource = notionProvider.getNotionStageDatasource(notionTimetableDataSourceId);

            //노션 category 조회
            final NotionCategoryDatasourceResponse notionCategoryDatasource = notionProvider.getNotionCategoryDatasource(notionCategoryDataSourceId);

            //노션 timetable 조회
            final NotionTimetableDatasourceResponse notionTimetableDatasource = notionProvider.getNotionTimetableDatasource(notionStageDataSourceId);

            //연관 관계 노션 데이터베이스 id값 검증(stage, category)
            NotionRelationValidator.validateNotionRelationIds(notionTimetableDatasource, notionStageDatasource, notionCategoryDatasource);

            // timetable 엔티티 생성
            final Timetable savedTimetable = adminTimetableSaver.saveTimetable(
                    eventId,
                    timetableStartAt,
                    timetableEndAt,
                    notionTimetableDataSourceId,
                    notionCategoryDataSourceId,
                    notionStageDataSourceId
            );

            // stage 엔티티 생성
            final TimetableStage savedStage = adminTimetableSaver

            // category 엔티티 생성


            // timetable block 엔티티 생성

        } catch (final FeignException e) {

        } catch (final PermitIllegalStateException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_NOTION_RELATION_ID);
        }
    }

    private void verifyNotionRelationDatabaseId(final String relationId) {

    }
}
