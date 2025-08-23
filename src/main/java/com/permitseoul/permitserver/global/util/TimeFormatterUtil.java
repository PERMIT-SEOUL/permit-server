package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.global.exception.TimeFormatException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeFormatterUtil {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm"); //17:00

    //시간 포맷팅 -> 시작일의 시작시간 - 종료일의 종료시간, ex) 17:00 - 19:00
    public static String formatEventTime(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new TimeFormatException(ErrorCode.INTERNAL_TIME_FORMAT_ERROR);
        }
        return startDateTime.format(TIME_FORMATTER) + "-" + endDateTime.format(TIME_FORMATTER);
    }
}
