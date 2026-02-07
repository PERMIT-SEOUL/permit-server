package com.permitseoul.permitserver.domain.admin.util;

import com.permitseoul.permitserver.domain.admin.timetable.base.core.exception.NotionPublicUrlNotFoundException;
import com.permitseoul.permitserver.domain.admin.timetable.base.core.exception.NotionUrlMalformedException;
import com.permitseoul.permitserver.domain.admin.util.exception.PermitListSizeNotMatchException;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity.TimetableBlockMediaEntity;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.global.exception.PermitIllegalStateException;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
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
            final NotionTimetableDatasourceResponse.NotionPage notionPage = notionTimetableDatasourceResponse.results().get(i);
            final String pageId = notionPage.id();
            final String host = getHost(notionPage);

            final NotionTimetableDatasourceResponse.FilesProperty filesProperty = notionPage.properties().media();
            if (filesProperty == null || filesProperty.files() == null) continue;
            final List<NotionTimetableDatasourceResponse.FilesProperty.FileItem> files = filesProperty.files();

            for (int seq = 0; seq < files.size(); seq++) {
                final NotionTimetableDatasourceResponse.FilesProperty.FileItem fileItem = files.get(seq);
                if (fileItem == null || fileItem.file() == null) continue;

                final String originalUrl = fileItem.file().url();
                if (originalUrl == null || originalUrl.isBlank()) continue;

                //노션에서 주는 url은 유효기간이 있어서 proxyUrl로 우회해서 조회하기
                final String proxyUrl = NotionImageUrlUtil.buildProxyUrl(host, pageId, originalUrl);
                if (proxyUrl == null) continue;

                mediaEntities.add(TimetableBlockMediaEntity.create(
                        block.getTimetableBlockId(),
                        seq,
                        proxyUrl
                ));
            }
        }

        return mediaEntities;
    }

    private static String getHost(NotionTimetableDatasourceResponse.NotionPage notionPage) {
        final String publicUrl = notionPage.publicUrl();
        if (publicUrl == null || publicUrl.isBlank()) {
            throw new NotionPublicUrlNotFoundException();
        }

        final String host;
        try {
            host = new URL(publicUrl).getHost();
        } catch (MalformedURLException e) {
            throw new NotionUrlMalformedException();
        }
        if (host == null) {
            throw new NotionUrlMalformedException();
        }
        return host;
    }
}
