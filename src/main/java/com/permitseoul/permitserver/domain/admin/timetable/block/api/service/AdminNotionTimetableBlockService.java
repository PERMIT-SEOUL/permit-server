package com.permitseoul.permitserver.domain.admin.timetable.block.api.service;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateStrategyManager;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.global.exception.LocalDateTimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotionTimetableBlockService {
    private final NotionTimetableBlockUpdateStrategyManager notionTimetableBlockUpdateStrategyManager;

    @Transactional
    public void updateNotionTimetableBlock(final NotionTimetableBlockUpdateWebhookRequest webhookRequest) {
        try {
            final NotionTimetableBlockWebhookType type = NotionTimetableBlockWebhookType.from(webhookRequest.data().properties());
            if (type == NotionTimetableBlockWebhookType.UNKNOWN) {
                log.error("알 수 없는 노션 timetable block TYPE 입니다. request={}", webhookRequest);
                return;
            }

            final NotionTimetableBlockUpdateWebhookStrategy strategy = notionTimetableBlockUpdateStrategyManager.getStrategy(type);
            if (strategy == null) {
                log.error("알 수 없는 노션 timetable block strategy 입니다. type={}", type);
                return;
            }


            strategy.updateNotionTimetableBlockByNotionWebhook(webhookRequest);
        } catch (TimetableBlockNotfoundException e) {
            log.error("timetable block row를 찾을 수 없습니다. request={}", webhookRequest, e);
        } catch (LocalDateTimeException e) {
            log.error("잘못된 날짜 순서입니다. request={}", webhookRequest, e);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            log.error("웹훅 데이터에 필수 필드가 누락되었습니다. request={}", webhookRequest, e);
        } catch (Exception e) {
            log.error("카테고리 웹훅 처리 중 알 수 없는 예외 발생. request={}", webhookRequest, e);
        }
    }
}
