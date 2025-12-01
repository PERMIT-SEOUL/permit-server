package com.permitseoul.permitserver.domain.admin.util;

import com.permitseoul.permitserver.domain.admin.util.exception.PermitListSizeNotMatchException;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity.TimetableBlockMediaEntity;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import com.permitseoul.permitserver.global.exception.PermitIllegalStateException;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public final class NotionResponseMapper {
    private static final int FIRST_INDEX = 0;

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

                    final String artist = props.artistOrActivity().title().isEmpty()
                            ? ""
                            : props.artistOrActivity().title().get(FIRST_INDEX).plain_text();
                    final String blockName = artist; // (현재 정해진 바로는)blockName == artistOrActivity
                    final String information = props.details() != null && props.details().richText() != null && !props.details().richText().isEmpty()
                            ? props.details().richText().get(FIRST_INDEX).plain_text()
                            : "";
                    final String redirectUrl = props.directUrl() != null ? props.directUrl().url() : null;

                    final LocalDateTime startAt = LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime(props.time().date().start());
                    final LocalDateTime endAt = LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime(props.time().date().end());

                    if (props.category().relation().isEmpty()) {
                        throw new PermitIllegalStateException();
                    }
                    if (props.stage().relation().isEmpty()) {
                        throw new PermitIllegalStateException();
                    }

                    final String categoryNotionId = props.category().relation().get(FIRST_INDEX).id();
                    final String stageNotionId = props.stage().relation().get(FIRST_INDEX).id();

                    return TimetableBlockEntity.create(
                            timetableId,
                            categoryNotionId,
                            stageNotionId,
                            startAt,
                            endAt,
                            blockName,
                            artist,
                            information,
                            redirectUrl,
                            result.id()
                    );
                })
                .collect(Collectors.toList());
    }

    public static List<TimetableBlockMediaEntity> mapToTimetableBlockMediaEntities(final List<TimetableBlock> savedBlocks,
                                                                                   final NotionTimetableDatasourceResponse notionTimetableDatasourceResponse) {
        if(savedBlocks.size() != notionTimetableDatasourceResponse.results().size()) {
            throw new PermitListSizeNotMatchException();
        }
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
