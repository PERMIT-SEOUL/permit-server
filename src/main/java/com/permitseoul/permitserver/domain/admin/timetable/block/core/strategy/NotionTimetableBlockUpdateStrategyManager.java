package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy;

import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.domain.NotionTimetableBlockWebhookType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotionTimetableBlockUpdateStrategyManager {
    private final Map<NotionTimetableBlockWebhookType, NotionTimetableBlockUpdateWebhookStrategy> strategyMap;

    public NotionTimetableBlockUpdateStrategyManager(final List<NotionTimetableBlockUpdateWebhookStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NotionTimetableBlockUpdateWebhookStrategy::getType, Function.identity()));
    }

    public NotionTimetableBlockUpdateWebhookStrategy getStrategy(final NotionTimetableBlockWebhookType notionTimetableBlockWebhookType) {
        return strategyMap.get(notionTimetableBlockWebhookType);
    }
}
