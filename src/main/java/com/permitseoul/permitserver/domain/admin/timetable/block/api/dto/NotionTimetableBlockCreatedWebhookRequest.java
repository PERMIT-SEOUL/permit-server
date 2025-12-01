package com.permitseoul.permitserver.domain.admin.timetable.block.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record NotionTimetableBlockCreatedWebhookRequest(
        WebhookData data
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WebhookData(
            String id,   // page id
            Parent parent
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Parent(
                @JsonProperty("data_source_id")
                String dataSourceId
        ) { }
    }
}
