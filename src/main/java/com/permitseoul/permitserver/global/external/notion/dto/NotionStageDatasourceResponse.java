package com.permitseoul.permitserver.global.external.notion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NotionStageDatasourceResponse(
        List<StagePage> results
) {

    public record StagePage(
            String id,
            StageProperties properties
    ) {}

    public record StageProperties(
            @JsonProperty("sequence(start from 0)")
            NumberProperty sequence,
            @JsonProperty("stage name")
            TitleProperty stageName
    ) {}

    // number 필드
    public record NumberProperty(
            Double number
    ) {}

    // title 필드
    public record TitleProperty(
            List<TitleItem> title
    ) {
        public record TitleItem(
                Text text,
                String plain_text
        ) {
            public record Text(String content) {}
        }
    }
}

