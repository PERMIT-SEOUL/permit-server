package com.permitseoul.permitserver.domain.admin.timetable.block.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NotionTimetableBlockUpdateWebhookRequest(
        NotionTimetableData data
) {
    public record NotionTimetableData(
            String id, // 노션 page id (== timetable row id)
            @JsonProperty("last_edited_time")
            String lastEditedTime,
            NotionTimetableParent parent,
            NotionTimetableProperties properties
    ) {
    }

    public record NotionTimetableParent(
            @JsonProperty("data_source_id")
            String dataSourceId,
            @JsonProperty("database_id")
            String databaseId
    ) {
    }

    public record NotionTimetableProperties(
            // Aa artist/activity (title)
            @JsonProperty("artist/activity")
            NotionTitleProperty artistActivity,
            // date time
            NotionDateProperty time,
            // stage relation
            NotionRelationProperty stage,
            // category relation
            NotionRelationProperty category,
            // media (files) - media 변경 시 실제 url 정보는 여기서 가져올 수 있음
            NotionFilesProperty media,
            // use when media changed (트리거용)
            @JsonProperty("use when media changed")
            NotionRichTextProperty useWhenMediaChanged,
            // redirect url
            @JsonProperty("redirect url")
            NotionRichTextProperty redirectUrl,
            // details (텍스트/셀렉트)
            NotionRichTextProperty details
    ) {
    }

    // ===== 공통 property 타입들 =====

    public record NotionDateProperty(
            NotionDateValue date
    ) {
    }

    public record NotionDateValue(
            String start,
            String end
    ) {
    }

    public record NotionRelationProperty(
            List<NotionRelationValue> relation
    ) {
    }

    public record NotionRelationValue(
            String id
    ) {
    }

    public record NotionTitleProperty(
            List<NotionTitleValue> title
    ) {
    }

    public record NotionTitleValue(
            @JsonProperty("plain_text")
            String plainText
    ) {
    }

    // media(files)
    public record NotionFilesProperty(
            List<NotionFileValue> files
    ) {
    }

    public record NotionFileValue(
            String name,
            NotionFileInner file
    ) {
    }

    public record NotionFileInner(
            String url
    ) {
    }

    // 리치 텍스트 등
    public record NotionRichTextProperty(
            @JsonProperty("rich_text")
            List<NotionRichTextValue> richText
    ) {
    }

    public record NotionRichTextValue(
            @JsonProperty("plain_text")
            String plainText
    ) {
    }
}
