package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class ConflictTimetableException extends TimetableApiException {
  public ConflictTimetableException(ErrorCode errorCode) {
    super(errorCode);
  }
}
