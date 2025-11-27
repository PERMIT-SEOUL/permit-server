package com.permitseoul.permitserver.domain.admin.timetable.stage.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.timetable.stage.api.dto.NotionTimetableStageUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.domain.NotionTimetableStageWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.NotionTimetableStageUpdateStrategyManager;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.NotionTimetableStageUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.exception.TimetableStageNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.exception.TimetableNotFoundException;
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

    @Transactional
    public void updateNotionTimetableStage(final NotionTimetableStageUpdateWebhookRequest notionTimetableStageUpdateWebhookRequest) {
        final NotionTimetableStageWebhookType notionTimetableStageWebhookType = NotionTimetableStageWebhookType.from(notionTimetableStageUpdateWebhookRequest.data().properties());

        if (notionTimetableStageWebhookType == NotionTimetableStageWebhookType.UNKNOWN) {
            log.warn("알 수 없는 NotionStageWebhookType 입니다. request={}", notionTimetableStageUpdateWebhookRequest);
            return;
        }

        final NotionTimetableStageUpdateWebhookStrategy notionTimetableStageUpdateWebhookStrategy = strategyManager.getStrategy(notionTimetableStageWebhookType);
        if (notionTimetableStageUpdateWebhookStrategy == null) {
            log.error("맞는 전략이 없습니다. request={}", notionTimetableStageUpdateWebhookRequest);
            return;
        }

        try {
            notionTimetableStageUpdateWebhookStrategy.updateNotionTimetableStageByNotionWebhook(notionTimetableStageUpdateWebhookRequest);
        } catch (TimetableStageNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TIMETABLE_STAGE);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            log.error("웹훅 데이터에 필수 필드가 누락되었습니다. request={}, ", notionTimetableStageUpdateWebhookRequest, e);
        }
    }
}
