package com.permitseoul.permitserver.domain.admin.util;

import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;

import java.util.List;
import java.util.stream.Collectors;

public final class NotionResponseMapper {

    public static List<TimetableStageEntity> mapToTimetableStageEntities(final long timetableId,
                                                                   final NotionStageDatasourceResponse notionStageDatasource) {

        return notionStageDatasource.results().stream()
                .map(result -> {
                    final String notionId = result.id();
                    final String stageName = result.properties()
                            .stageName()
                            .title()
                            .stream()
                            .findFirst()
                            .map(NotionStageDatasourceResponse.TitleProperty.TitleItem::plain_text)
                            .orElse("");
                    final int sequence = result.properties()
                            .sequence()
                            .number() != null
                            ? result.properties().sequence().number().intValue()
                            : -1;

                    return TimetableStageEntity.create(timetableId, stageName, sequence, notionId);
                })
                .collect(Collectors.toList());
    }

    public static List<TimetableCategoryEntity> mapToTimetableCategoryEntities(final long timetableId,
                                                                         final NotionCategoryDatasourceResponse notionCategoryDatasource) {

        return notionCategoryDatasource.results().stream()
                .map(result -> {
                    final String notionRowId = result.id();
                    final String categoryName = result.properties()
                            .categoryName()
                            .title()
                            .stream()
                            .findFirst()
                            .map(NotionCategoryDatasourceResponse.TitleProperty.TitleItem::plain_text)
                            .orElse("");
                    final String backgroundColor = result.properties()
                            .backgroundColor()
                            .richText()
                            .stream()
                            .findFirst()
                            .map(NotionCategoryDatasourceResponse.RichTextProperty.RichText::plain_text)
                            .orElse("");
                    final String lineColor = result.properties()
                            .lineColor()
                            .richText()
                            .stream()
                            .findFirst()
                            .map(NotionCategoryDatasourceResponse.RichTextProperty.RichText::plain_text)
                            .orElse("");

                    return TimetableCategoryEntity.create(
                            timetableId,
                            categoryName,
                            backgroundColor,
                            lineColor,
                            notionRowId
                    );
                })
                .collect(Collectors.toList());
    }
}
