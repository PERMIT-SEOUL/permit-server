package com.permitseoul.permitserver.global.external.notion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record NotionTimetableDatasourceResponse(
        List<NotionPage> results
) {

    public record NotionPage(
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
            TitleProperty artist
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
                String name,
                FileDetail file
        ) {
            public record FileDetail(
                    String url,
                    String expiry_time
            ) {}
        }
    }

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
