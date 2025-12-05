package com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy;

import com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy.domain.NotionTimetableCategoryWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.NotionTimetableStageUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.admin.timetable.stage.core.strategy.domain.NotionTimetableStageWebhookType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotionTimetableCategoryUpdateStrategyManager {
    private final Map<NotionTimetableCategoryWebhookType, NotionTimetableCategoryUpdateWebhookStrategy> strategyMap;

    public NotionTimetableCategoryUpdateStrategyManager(final List<NotionTimetableCategoryUpdateWebhookStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NotionTimetableCategoryUpdateWebhookStrategy::getType, Function.identity()));
    }

    public NotionTimetableCategoryUpdateWebhookStrategy getStrategy(final NotionTimetableCategoryWebhookType notionTimetableCategoryWebhookType) {
        return strategyMap.get(notionTimetableCategoryWebhookType);
    }

}
