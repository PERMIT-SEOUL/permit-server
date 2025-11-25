package com.permitseoul.permitserver.domain.admin.timetable.block.api.service;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateStrategyManager;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateWebhookStrategy;
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
    public void updateNotionTimetableBlock(final NotionTimetableBlockUpdateWebhookRequest request) {
        final NotionTimetableBlockWebhookType type = NotionTimetableBlockWebhookType.from(request.data().properties());
        if (type == NotionTimetableBlockWebhookType.UNKNOWN) {
            log.error("알 수 없는 노션 timetable block TYPE 입니다. request={}", request);
            return;
        }

        final NotionTimetableBlockUpdateWebhookStrategy strategy = notionTimetableBlockUpdateStrategyManager.getStrategy(type);
        if (strategy == null) {
            log.error("알 수 없는 노션 timetable block TYPE 입니다. type={}", type);
            return;
        }
        strategy.updateNotionTimetableBlockByNotionWebhook(request);
    }
}
