package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class NotfoundTimetableException extends TimetableApiException {
    public NotfoundTimetableException(ErrorCode errorCode) {
        super(errorCode);
    }
}
