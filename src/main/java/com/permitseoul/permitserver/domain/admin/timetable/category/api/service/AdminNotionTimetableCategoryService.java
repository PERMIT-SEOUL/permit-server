package com.permitseoul.permitserver.domain.admin.timetable.category.api.service;

import com.permitseoul.permitserver.domain.admin.timetable.category.api.dto.NotionTimetableCategoryUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy.NotionTimetableCategoryUpdateStrategyManager;
import com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy.NotionTimetableCategoryUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy.domain.NotionTimetableCategoryWebhookType;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.exception.TimetableCategoryNotfoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotionTimetableCategoryService {
    private final NotionTimetableCategoryUpdateStrategyManager notionTimetableCategoryUpdateStrategyManager;

    public void updateNotionTimetableCategory(final NotionTimetableCategoryUpdateWebhookRequest notionTimetableCategoryUpdateWebhookRequest) {
        try {
            final NotionTimetableCategoryWebhookType webhookType = NotionTimetableCategoryWebhookType.from(notionTimetableCategoryUpdateWebhookRequest.data().properties());
            if (webhookType == NotionTimetableCategoryWebhookType.UNKNOWN) {
                log.warn("알 수 없는 NotionCategoryWebhookType입니다. request={}", notionTimetableCategoryUpdateWebhookRequest);
                return;
            }

            final NotionTimetableCategoryUpdateWebhookStrategy strategy = notionTimetableCategoryUpdateStrategyManager.getStrategy(webhookType);
            if (strategy == null) {
                log.error("NotionCategoryUpdateWebhookStrategy 찾을 수 없습니다. type={}, request={}", webhookType, notionTimetableCategoryUpdateWebhookRequest);
                return;
            }

            strategy.updateNotionTimetableStageByNotionWebhook(notionTimetableCategoryUpdateWebhookRequest);

        } catch (NullPointerException e) {
            log.error("웹훅 데이터에 필수 필드가 누락되었습니다. request={}", notionTimetableCategoryUpdateWebhookRequest);
        } catch (TimetableCategoryNotfoundException e) {
            log.error("timetableCategory를 찾을 수 없습니다.. request={}", notionTimetableCategoryUpdateWebhookRequest);
        } catch (Exception e) {
            log.error("카테고리 웹훅 처리 중 알 수 없는 예외 발생. request={}", notionTimetableCategoryUpdateWebhookRequest);
        }
    }
}
