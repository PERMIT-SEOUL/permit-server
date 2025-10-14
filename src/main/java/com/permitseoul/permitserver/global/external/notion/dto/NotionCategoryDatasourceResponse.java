package com.permitseoul.permitserver.global.external.notion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NotionCategoryDatasourceResponse(
        List<CategoryPage> results
) {

    public record CategoryPage(
            String id,
            CategoryProperties properties
    ) {}

    public record CategoryProperties(
            @JsonProperty("background color")
            RichTextProperty backgroundColor,
            @JsonProperty("line color")
            RichTextProperty lineColor,
            @JsonProperty("category name")
            TitleProperty categoryName
    ) {}

    // text 필드
    public record RichTextProperty(
            @JsonProperty("rich_text")
            List<RichText> richText
    ) {
        public record RichText(
                String type,
                Text text,
                String plain_text
        ) {
            public record Text(String content) {}
        }
    }

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
