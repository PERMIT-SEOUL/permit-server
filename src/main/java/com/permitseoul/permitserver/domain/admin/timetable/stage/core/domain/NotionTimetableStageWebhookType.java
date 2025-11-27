package com.permitseoul.permitserver.domain.admin.timetable.stage.core.domain;

import com.permitseoul.permitserver.domain.admin.timetable.stage.api.dto.NotionTimetableStageUpdateWebhookRequest;

public enum NotionTimetableStageWebhookType {
    NAME,
    SEQUENCE,
    UNKNOWN;

    public static NotionTimetableStageWebhookType from(
            NotionTimetableStageUpdateWebhookRequest.NotionStageProperties props
    ) {
        if (props.stageName() != null
                && props.stageName().title() != null
                && !props.stageName().title().isEmpty()) {
            return NAME;
        }

        if (props.sequence() != null && props.sequence().number() != null) {
            return SEQUENCE;
        }

        return UNKNOWN;
    }
}
