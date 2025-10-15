package com.permitseoul.permitserver.domain.admin.util;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity.TimetableBlockMediaEntity;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public final class NotionResponseMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

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

    public static List<TimetableBlockEntity> mapToTimetableBlockEntities(final long timetableId,
                                                                         final NotionTimetableDatasourceResponse notionTimetableDatasourceResponse) {
        return notionTimetableDatasourceResponse.results().stream()
                .map(result -> {
                    final NotionTimetableDatasourceResponse.NotionProperties props = result.properties();

                    final String artist = props.artist().title().isEmpty()
                            ? ""
                            : props.artist().title().get(0).plain_text();
                    final String blockName = artist; // (현재 정해진 바로는)blockName == artist
                    final String information = props.details() != null && props.details().richText() != null && !props.details().richText().isEmpty()
                            ? props.details().richText().get(0).plain_text()
                            : "";
                    final String redirectUrl = props.directUrl() != null ? props.directUrl().url() : null;

                    final LocalDateTime startAt = LocalDateTime.parse(props.time().date().start(), FORMATTER);
                    final LocalDateTime endAt = LocalDateTime.parse(props.time().date().end(), FORMATTER);

                    final String categoryNotionId = props.category().relation().get(0).id();
                    final String stageNotionId = props.stage().relation().get(0).id();

                    return TimetableBlockEntity.create(
                            timetableId,
                            categoryNotionId,
                            stageNotionId,
                            startAt,
                            endAt,
                            blockName,
                            artist,
                            information,
                            redirectUrl
                    );
                })
                .collect(Collectors.toList());
    }

    public static List<TimetableBlockMediaEntity> mapToTimetableBlockMediaEntities(final List<TimetableBlock> savedBlocks,
                                                                                   final NotionTimetableDatasourceResponse notionTimetableDatasourceResponse) {
        final List<TimetableBlockMediaEntity> mediaEntities = new ArrayList<>();

        for (int i = 0; i < savedBlocks.size(); i++) {
            final TimetableBlock block = savedBlocks.get(i);
            final List<NotionTimetableDatasourceResponse.FilesProperty.FileItem> files = notionTimetableDatasourceResponse.results().get(i).properties().media().files();
            if (files == null) continue;

            for (int seq = 0; seq < files.size(); seq++) {
                String mediaUrl = files.get(seq).file().url();
                mediaEntities.add(TimetableBlockMediaEntity.create(
                        block.getTimetableBlockId(),
                        seq,
                        mediaUrl
                ));
            }
        }

        return mediaEntities;
    }
}
