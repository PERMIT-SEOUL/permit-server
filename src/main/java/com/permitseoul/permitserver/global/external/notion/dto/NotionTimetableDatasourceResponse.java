package com.permitseoul.permitserver.global.external.notion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record NotionTimetableDatasourceResponse(
        List<NotionPage> results
) {

    public record NotionPage(
            String id,
            Parent parent,
            NotionProperties properties
    ) {}

    public record Parent(
            @JsonProperty("data_source_id")
            String dataSourceId
    ) {}

    public record NotionProperties(
            RelationProperty stage,
            DateProperty time,
            FilesProperty media,
            @JsonProperty("use when media changed")
            RichTextProperty useWhenMediaChanged,
            RelationProperty category,
            @JsonProperty("artist/activity")
            TitleProperty artistOrActivity,
            @JsonProperty("details")
            RichTextProperty details,
            @JsonProperty("direct_url")
            UrlProperty directUrl
    ) {}

    // URL 필드
    public record UrlProperty(
            String url
    ) {}

    // 연관된 테이블 row 필드
    public record RelationProperty(
            List<RelationItem> relation
    ) {
        public record RelationItem(String id) {}
    }

    // date 필드
    public record DateProperty(
            DateValue date
    ) {
        public record DateValue(
                String start,
                String end,
                String time_zone
        ) {}
    }

    // media 필드
    public record FilesProperty(
            List<FileItem> files
    ) {
        public record FileItem(
                @JsonProperty("file")
                FileDetail file
        ) {
            public record FileDetail(
                    String url
            ) {}
        }
    }

    // text 필드
    public record RichTextProperty(
            @JsonProperty("rich_text")
            List<RichText> richText
    ) {
        public record RichText(
                String plain_text
        ) {}
    }

    // title 필드
    public record TitleProperty(
            List<TitleItem> title
    ) {
        public record TitleItem(
                String plain_text
        ) {}
    }
}
