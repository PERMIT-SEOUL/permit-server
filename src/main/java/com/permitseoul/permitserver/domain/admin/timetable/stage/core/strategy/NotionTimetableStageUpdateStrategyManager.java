package com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy;

import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.domain.NotionTimetableStageWebhookType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotionTimetableStageUpdateStrategyManager {
    private final Map<NotionTimetableStageWebhookType, NotionTimetableStageUpdateWebhookStrategy> strategyMap;

    public NotionTimetableStageUpdateStrategyManager(final List<NotionTimetableStageUpdateWebhookStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NotionTimetableStageUpdateWebhookStrategy::getType, Function.identity()));
    }

    public NotionTimetableStageUpdateWebhookStrategy getStrategy(final NotionTimetableStageWebhookType notionTimetableStageWebhookType) {
        return strategyMap.get(notionTimetableStageWebhookType);
    }
}
