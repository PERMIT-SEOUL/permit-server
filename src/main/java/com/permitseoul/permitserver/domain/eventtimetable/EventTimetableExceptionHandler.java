package com.permitseoul.permitserver.domain.eventtimetable;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception.TimetableApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.eventtimetable")
public class EventTimetableExceptionHandler {

    @ExceptionHandler(TimetableApiException.class)
    public ResponseEntity<BaseResponse<?>> handleTimetableApiException(final TimetableApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }

}
