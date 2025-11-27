package com.permitseoul.permitserver.domain.admin.timetable.stage.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NotionTimetableStageUpdateWebhookRequest(
        NotionStageData data

) {
    public record NotionStageData(
            String id, // 노션 stage row(page) id
            @JsonProperty("last_edited_time")
            String lastEditedTime,
            NotionStageParent parent,
            NotionStageProperties properties
    ) {}

    public record NotionStageParent(
            @JsonProperty("data_source_id")
            String dataSourceId,
            @JsonProperty("database_id")
            String databaseId
    ) {}

    public record NotionStageProperties(
            @JsonProperty("stage name")
            NotionTitleProperty stageName,
            @JsonProperty("sequence(start from 0)")
            NotionNumberProperty sequence
    ) {}

    public record NotionTitleProperty(
            List<NotionTitleValue> title
    ) {}

    public record NotionTitleValue(
            @JsonProperty("plain_text")
            String plainText
    ) {}

    public record NotionNumberProperty(
            Double number
    ) {}
}
