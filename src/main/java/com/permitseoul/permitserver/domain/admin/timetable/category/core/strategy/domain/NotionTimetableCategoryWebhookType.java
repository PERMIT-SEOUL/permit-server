package com.permitseoul.permitserver.domain.admin.timetable.category.core.strategy.domain;

import com.permitseoul.permitserver.domain.admin.timetable.category.api.dto.NotionTimetableCategoryUpdateWebhookRequest;

public enum NotionTimetableCategoryWebhookType {
    NAME,
    BACKGROUND_COLOR,
    LINE_COLOR,
    UNKNOWN;

    public static NotionTimetableCategoryWebhookType from(
            NotionTimetableCategoryUpdateWebhookRequest.NotionCategoryProperties props
    ) {
        if (props == null) {
            return UNKNOWN;
        }

        if (props.categoryName() != null && props.categoryName().title() != null && !props.categoryName().title().isEmpty()) {
            return NAME;
        }

        if (props.backgroundColor() != null && props.backgroundColor().richText() != null && !props.backgroundColor().richText().isEmpty()) {
            return BACKGROUND_COLOR;
        }

        if (props.lineColor() != null && props.lineColor().richText() != null && !props.lineColor().richText().isEmpty()) {
            return LINE_COLOR;
        }

        return UNKNOWN;
    }
}
