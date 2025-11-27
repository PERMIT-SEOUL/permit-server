package com.permitseoul.permitserver.domain.admin.timetable.category.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NotionTimetableCategoryUpdateWebhookRequest(
        NotionCategoryData data
) {

    public record NotionCategoryData(
            String id,
            @JsonProperty("last_edited_time")
            String lastEditedTime,
            NotionCategoryParent parent,
            NotionCategoryProperties properties
    ) {}

    public record NotionCategoryParent(
            @JsonProperty("data_source_id")
            String dataSourceId,
            @JsonProperty("database_id")
            String databaseId
    ) {}

    public record NotionCategoryProperties(
            @JsonProperty("category name")
            NotionTitleProperty categoryName,
            @JsonProperty("background color")
            NotionRichTextProperty backgroundColor,
            @JsonProperty("line color")
            NotionRichTextProperty lineColor
    ) {}

    // ===== 공통 property 타입들 =====

    public record NotionTitleProperty(
            List<NotionTitleValue> title
    ) {}

    public record NotionTitleValue(
            @JsonProperty("plain_text")
            String plainText
    ) {}

    public record NotionRichTextProperty(
            @JsonProperty("rich_text")
            List<NotionRichTextValue> richText
    ) {}

    public record NotionRichTextValue(
            @JsonProperty("plain_text")
            String plainText
    ) {}
}
