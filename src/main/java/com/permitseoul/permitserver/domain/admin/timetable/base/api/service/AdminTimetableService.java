package com.permitseoul.permitserver.domain.admin.timetable.base.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.timetable.base.api.dto.req.TimetableUpdateRequest;
import com.permitseoul.permitserver.domain.admin.timetable.base.api.dto.res.TimetableInfoResponse;
import com.permitseoul.permitserver.domain.admin.timetable.base.core.components.AdminTimetableRetriever;
import com.permitseoul.permitserver.domain.admin.timetable.base.core.components.AdminTimetableUpdater;
import com.permitseoul.permitserver.domain.admin.util.exception.PermitListSizeNotMatchException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity.TimetableEntity;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.exception.TimetableNotFoundException;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import com.permitseoul.permitserver.global.exception.LocalDateTimeException;
import com.permitseoul.permitserver.global.exception.PermitIllegalStateException;
import com.permitseoul.permitserver.global.external.notion.NotionProvider;
import com.permitseoul.permitserver.global.external.notion.NotionRelationValidator;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.exception.NotFoundNotionResponseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminTimetableService {
    private final NotionProvider notionProvider;
    private final AdminTimetableFacade adminTimetableFacade;
    private final AdminTimetableRetriever adminTimetableRetriever;
    private final AdminTimetableUpdater adminTimetableUpdater;

    public void saveInitialTimetableInfo(final long eventId,
                                         final LocalDateTime timetableStartAt,
                                         final LocalDateTime timetableEndAt,
                                         final String notionTimetableDataSourceId,
                                         final String notionStageDataSourceId,
                                         final String notionCategoryDataSourceId
    ) {
        try {

            //노션 stage 조회
            final NotionStageDatasourceResponse notionStageDatasourceResponse = notionProvider.getNotionStageDatasource(notionStageDataSourceId);

            //노션 category 조회
            final NotionCategoryDatasourceResponse notionCategoryDatasourceResponse = notionProvider.getNotionCategoryDatasource(notionCategoryDataSourceId);

            //노션 timetable 조회
            final NotionTimetableDatasourceResponse notionTimetableDatasourceResponse = notionProvider.getNotionTimetableDatasource(notionTimetableDataSourceId);

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
                    notionTimetableDatasourceResponse,
                    notionStageDatasourceResponse,
                    notionCategoryDatasourceResponse
            );

        } catch (final FeignException e) {
            throw new AdminApiException(ErrorCode.INTERNAL_NOTION_FEIGN_ERROR);
        } catch (final PermitIllegalStateException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_NOTION_RELATION_ID);
        } catch (DateFormatException e) {
            throw new AdminApiException(ErrorCode.INTERNAL_ISO_DATE_ERROR);
        } catch (final PermitListSizeNotMatchException e) {
            throw new AdminApiException(ErrorCode.BAD_REQUEST_MISMATCH_LIST_SIZE);
        } catch (final NotFoundNotionResponseException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_NOTION_DATABASE_SOURCE);
        } catch (final LocalDateTimeException e) {
            throw new AdminApiException(ErrorCode.BAD_REQUEST_DATE_TIME_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public TimetableInfoResponse getTimetableInfo(final long eventId) {
        final Timetable timetable;
        try {
            timetable = adminTimetableRetriever.findTimetableByEventId(eventId);
        } catch (TimetableNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TIMETABLE);
        }

        return TimetableInfoResponse.of(
                timetable.getTimetableId(),
                LocalDateTimeFormatterUtil.formatyyyyMMdd(timetable.getStartAt()),
                LocalDateTimeFormatterUtil.formatHHmm(timetable.getStartAt()),
                LocalDateTimeFormatterUtil.formatyyyyMMdd(timetable.getEndAt()),
                LocalDateTimeFormatterUtil.formatHHmm(timetable.getEndAt()),
                timetable.getNotionTimetableDatasourceId(),
                timetable.getNotionCategoryDatasourceId(),
                timetable.getNotionStageDatasourceId()
        );
    }

    @Transactional
    public void updateTimetable(final long timetableId,
                                final LocalDateTime timetableStartAt,
                                final LocalDateTime timetableEndAt,
                                final String notionTimetableDataSourceId,
                                final String notionCategoryDataSourceId,
                                final String notionStageDataSourceId) {
        final TimetableEntity timetableEntity;
        try {
            timetableEntity = adminTimetableRetriever.findTimetableEntityById(timetableId);
            adminTimetableUpdater.updateTimetable(
                    timetableEntity,
                    timetableStartAt,
                    timetableEndAt,
                    notionTimetableDataSourceId,
                    notionCategoryDataSourceId,
                    notionStageDataSourceId
            );
        } catch (TimetableNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TIMETABLE);
        } catch (LocalDateTimeException e) {
            throw new AdminApiException(ErrorCode.BAD_REQUEST_DATE_TIME_ERROR);
        }
    }
}
