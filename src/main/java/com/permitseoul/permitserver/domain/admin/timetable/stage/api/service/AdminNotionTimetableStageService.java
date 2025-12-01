package com.permitseoul.permitserver.domain.admin.timetable.stage.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.timetable.base.api.exception.AdminNotionException;
import com.permitseoul.permitserver.domain.admin.timetable.base.core.components.AdminTimetableRetriever;
import com.permitseoul.permitserver.domain.admin.timetable.stage.api.dto.NotionTimetableStageUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.component.AdminTimetableStageSaver;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.domain.NotionTimetableStageWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.NotionTimetableStageUpdateStrategyManager;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.NotionTimetableStageUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.exception.TimetableStageNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.exception.TimetableNotFoundException;
import com.permitseoul.permitserver.global.external.notion.exception.NotFoundNotionResponseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotionTimetableStageService {
    private final NotionTimetableStageUpdateStrategyManager strategyManager;
    private final AdminTimetableRetriever adminTimetableRetriever;
    private final AdminTimetableStageSaver adminTimetableStageSaver;

    @Transactional
    public void updateNotionTimetableStage(final NotionTimetableStageUpdateWebhookRequest notionTimetableStageUpdateWebhookRequest) {
        try {
            final NotionTimetableStageWebhookType notionTimetableStageWebhookType = NotionTimetableStageWebhookType.from(notionTimetableStageUpdateWebhookRequest.data().properties());

            if (notionTimetableStageWebhookType == NotionTimetableStageWebhookType.UNKNOWN) {
                log.error("알 수 없는 NotionStageWebhookType 입니다. request={}", notionTimetableStageUpdateWebhookRequest);
                throw new AdminNotionException();
            }

            final NotionTimetableStageUpdateWebhookStrategy notionTimetableStageUpdateWebhookStrategy = strategyManager.getStrategy(notionTimetableStageWebhookType);
            if (notionTimetableStageUpdateWebhookStrategy == null) {
                log.error("맞는 전략이 없습니다. request={}", notionTimetableStageUpdateWebhookRequest);
                throw new AdminNotionException();
            }

            notionTimetableStageUpdateWebhookStrategy.updateNotionTimetableStageByNotionWebhook(notionTimetableStageUpdateWebhookRequest);
        } catch (TimetableStageNotFoundException e) {
            log.error("timetable Stage를 찾을 수 없습니다. request={}, ", notionTimetableStageUpdateWebhookRequest, e);
            throw new AdminNotionException();
        } catch (IndexOutOfBoundsException | NullPointerException | NotFoundNotionResponseException e) {
            log.error("웹훅 데이터에 필수 필드가 누락되었습니다. request={}, ", notionTimetableStageUpdateWebhookRequest, e);
            throw new AdminNotionException();
        } catch (Exception e) {
            log.error("스테이지 웹훅 처리 중 알 수 없는 예외 발생. request={}", notionTimetableStageUpdateWebhookRequest, e);
            throw new AdminNotionException();
        }
    }

    @Transactional
    public void saveNewTimetableStageRowWebhookRequest(final String notionTimetableStageDataSourceId, final String notionNewTimetableStageRowId) {
        try {
            final Timetable timetable = adminTimetableRetriever.findTimetableByTimetableStageDataSourceId(notionTimetableStageDataSourceId);
            adminTimetableStageSaver.saveTimetableStage(timetable.getTimetableId(), notionNewTimetableStageRowId);
        } catch (TimetableNotFoundException e) {
            log.error("노션 타임테이블을 찾을 수 없습니다. datasourceId = {}", notionTimetableStageDataSourceId, e);
            throw new AdminApiException(ErrorCode.NOT_FOUND_TIMETABLE);
        }
    }
}
