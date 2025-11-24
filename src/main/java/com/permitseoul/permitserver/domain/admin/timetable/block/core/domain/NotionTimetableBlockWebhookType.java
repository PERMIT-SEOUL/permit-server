package com.permitseoul.permitserver.domain.admin.timetable.block.core.domain;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockWebhookRequest;

public enum NotionTimetableBlockWebhookType {
    ARTIST,       // artist/activity(title) 변경
    TIME,         // time(date) 변경
    STAGE,        // stage(relation) 변경
    CATEGORY,     // category(relation) 변경
    MEDIA,        // media 파일 변경 (실제 트리거는 "use when media changed" 필드가 바뀌면 트리거)
    REDIRECT_URL, // redirect url 변경 (원하면 사용)
    DETAILS,      // details 필드 변경 (원하면 사용)
    UNKNOWN;

    public static NotionTimetableBlockWebhookType from(final NotionTimetableBlockWebhookRequest.NotionTimetableProperties props) {
        if (props.artistActivity() != null && props.artistActivity().title() != null
                && !props.artistActivity().title().isEmpty()) {
            return ARTIST;
        }
        if (props.time() != null && props.time().date() != null) {
            return TIME;
        }
        if (props.stage() != null && props.stage().relation() != null && !props.stage().relation().isEmpty()) {
            return STAGE;
        }
        if (props.category() != null && props.category().relation() != null && !props.category().relation().isEmpty()) {
            return CATEGORY;
        }
        if (props.useWhenMediaChanged() != null) {
            // media 자체는 files 필드로 들어오고,
            // use when media changed 필드는 단순 트리거 용도
            return MEDIA;
        }
        if (props.redirectUrl() != null && props.redirectUrl().url() != null) {
            return REDIRECT_URL;
        }
        if (props.details() != null) {
            return DETAILS;
        }
        return UNKNOWN;
    }
}
