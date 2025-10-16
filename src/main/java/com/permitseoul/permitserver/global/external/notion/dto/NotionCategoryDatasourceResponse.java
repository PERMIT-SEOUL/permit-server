package com.permitseoul.permitserver.global.external.notion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record NotionCategoryDatasourceResponse(
        List<CategoryPage> results
) {

    public record CategoryPage(
            String id,
            Parent parent,
            CategoryProperties properties
    ) {}

    public record Parent(
            @JsonProperty("data_source_id")
            String dataSourceId
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
                String plain_text
        ) { }
    }

    // title 필드
    public record TitleProperty(
            List<TitleItem> title
    ) {
        public record TitleItem(
                String plain_text
        ) { }
    }
}
