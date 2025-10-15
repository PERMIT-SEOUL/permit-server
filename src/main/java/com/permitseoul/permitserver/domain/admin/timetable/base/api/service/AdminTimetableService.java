package com.permitseoul.permitserver.domain.admin.timetable.base.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.AdminTimetableStageSaver;
import com.permitseoul.permitserver.global.exception.PermitIllegalStateException;
import com.permitseoul.permitserver.global.external.notion.NotionProvider;
import com.permitseoul.permitserver.domain.admin.timetable.base.core.components.AdminTimetableSaver;
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
    private final AdminTimetableStageSaver adminTimetableStageSaver;
    private final AdminTimetableFacade adminTimetableFacade;

    public void saveInitialTimetableInfo(final long eventId,
                                         final LocalDateTime timetableStartAt,
                                         final LocalDateTime timetableEndAt,
                                         final String notionTimetableDataSourceId,
                                         final String notionStageDataSourceId,
                                         final String notionCategoryDataSourceId
    ) {

        try {

            //노션 stage 조회
            final NotionStageDatasourceResponse notionStageDatasourceResponse = notionProvider.getNotionStageDatasource(notionTimetableDataSourceId);

            //노션 category 조회
            final NotionCategoryDatasourceResponse notionCategoryDatasourceResponse = notionProvider.getNotionCategoryDatasource(notionCategoryDataSourceId);

            //노션 timetable 조회
            final NotionTimetableDatasourceResponse notionTimetableDatasourceResponse = notionProvider.getNotionTimetableDatasource(notionStageDataSourceId);

            //연관 관계 노션 데이터베이스 id값 검증(stage, category)
            NotionRelationValidator.validateNotionRelationIds(notionTimetableDatasourceResponse, notionStageDatasourceResponse, notionCategoryDatasourceResponse);

            // timetable, timetableStage, timetableCategory, timetableBlock entity 생성
            adminTimetableFacade.saveInitialTimetableInfos(
                    eventId,
                    timetableStartAt,
                    timetableEndAt,
                    notionTimetableDataSourceId,
                    notionStageDataSourceId,
                    notionCategoryDataSourceId,
                    notionStageDatasourceResponse,
                    notionCategoryDatasourceResponse,
                    notionTimetableDatasourceResponse
            );

        } catch (final FeignException e) {
            throw new AdminApiException(ErrorCode.INTERNAL_NOTION_FEIGN_ERROR);
        } catch (final PermitIllegalStateException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_NOTION_RELATION_ID);
        }
    }
}
