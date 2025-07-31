package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.TimetableBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class TimetableApiException extends TimetableBaseException {
    private final ErrorCode errorCode;
}
