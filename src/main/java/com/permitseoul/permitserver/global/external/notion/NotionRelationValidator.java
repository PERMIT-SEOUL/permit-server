package com.permitseoul.permitserver.global.external.notion;

import com.permitseoul.permitserver.global.exception.PermitIllegalStateException;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class NotionRelationValidator {
    public static void validateNotionRelationIds(final NotionTimetableDatasourceResponse timetableRes,
                                                 final NotionStageDatasourceResponse stageRes,
                                                 final NotionCategoryDatasourceResponse categoryRes) {

        // Stage, Category ID Set 생성
        final Set<String> validStageIds = stageRes.results().stream()
                .map(NotionStageDatasourceResponse.StagePage::id)
                .collect(Collectors.toSet());

        final Set<String> validCategoryIds = categoryRes.results().stream()
                .map(NotionCategoryDatasourceResponse.CategoryPage::id)
                .collect(Collectors.toSet());

        // timetable에 있는 relation id 검증
        final boolean invalidStageExists = timetableRes.results().stream()
                .flatMap(page -> page.properties().stage().relation().stream())
                .map(NotionTimetableDatasourceResponse.RelationProperty.RelationItem::id)
                .anyMatch(id -> !validStageIds.contains(id));

        final boolean invalidCategoryExists = timetableRes.results().stream()
                .flatMap(page -> page.properties().category().relation().stream())
                .map(NotionTimetableDatasourceResponse.RelationProperty.RelationItem::id)
                .anyMatch(id -> !validCategoryIds.contains(id));

        if (invalidStageExists || invalidCategoryExists) {
            throw new PermitIllegalStateException();
        }
    }
}
