package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.domain.payment.api.dto.PaymentCancelResponse;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@UtilityClass
public final class LocalDateTimeFormatterUtil {
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E", Locale.ENGLISH); // Sun
    private static final DateTimeFormatter DAY_DD_FORMATTER = DateTimeFormatter.ofPattern("E, dd", Locale.ENGLISH);
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM", Locale.ENGLISH); // 2025.09
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 2025.08.15
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm"); // 17:30
    private static final DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH); // Jan
                                                                                                                       // 19
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH); // 2026
    private static final String COMMA_AND_SPACE = ", ";
    private static final String EN_DASH = " – ";

    public static String formatStartEndDate(final LocalDateTime startDate, final LocalDateTime endDate) {
        final StringBuilder sb = new StringBuilder();
        final String startDay = startDate.format(DAY_FORMATTER); // Mon
        final String endDay = endDate.format(DAY_FORMATTER); // Thu
        final String startMonthDay = startDate.format(MONTH_DAY_FORMATTER); // Jan 26
        final String endMonthDay = endDate.format(MONTH_DAY_FORMATTER); // Jan 29 or Feb 10
        final String year = endDate.format(YEAR_FORMATTER); // 2026

        // 시작날짜와 종료날짜가 같을 때, "Jan 19 (Mon), 2026" 형식으로 포맷팅
        if (startDate.toLocalDate().equals(endDate.toLocalDate())) {
            sb.append(startMonthDay)
                    .append(" (").append(startDay).append(")")
                    .append(COMMA_AND_SPACE)
                    .append(year);
        } else {
            // 시작날짜와 종료날짜가 다를 때 (같은 월 또는 다른 월 모두 동일한 형식)
            // "Jan 26 (Mon) – Jan 29 (Thu), 2026" 또는 "Jan 26 (Mon) – Feb 10 (Tue), 2026"
            sb.append(startMonthDay)
                    .append(" (").append(startDay).append(")")
                    .append(EN_DASH)
                    .append(endMonthDay)
                    .append(" (").append(endDay).append(")")
                    .append(COMMA_AND_SPACE)
                    .append(year);
        }
        return sb.toString();
    }

    // "Fri, 04" 포맷팅
    public static String formatDayWithDate(final LocalDateTime dateTime) {
        return dateTime.format(DAY_DD_FORMATTER);
    }

    // "2025.08" 포맷팅
    public static String formatYearMonth(final LocalDateTime dateTime) {
        return dateTime.format(YEAR_MONTH_FORMATTER);
    }

    // "2025.08.15" 포맷팅
    public static String formatyyyyMMdd(final LocalDateTime dateTime) {
        return dateTime.format(DATE);
    }

    // "17:30" 포맷팅
    public static String formatHHmm(final LocalDateTime dateTime) {
        return dateTime.format(TIME);
    }

    public static LocalDateTime combineDateAndTime(final LocalDate date, final LocalTime time) {
        return LocalDateTime.of(
                Objects.requireNonNull(date, "date가 null입니다."),
                Objects.requireNonNull(time, "time이 null입니다."));
    }

    // update할 때, 결합
    public static LocalDateTime combineDateAndTimeForUpdate(final LocalDate date, final LocalTime time,
            final LocalDateTime originalDateTime) {
        if (originalDateTime == null) {
            throw new DateFormatException();
        }
        if (date == null && time == null) {
            return originalDateTime;
        }
        final LocalDate localDate = date == null ? originalDateTime.toLocalDate() : date;
        final LocalTime localTime = time == null ? originalDateTime.toLocalTime() : time;
        return LocalDateTime.of(localDate, localTime);
    }

    // 날짜 형식 ISO 8601을 LocalDateTime로 변환
    public static LocalDateTime parseISO8601DateToLocalDateTime(final String isoDate) {
        if (isoDate == null || isoDate.isBlank()) {
            throw new DateFormatException();
        }
        return OffsetDateTime.parse(isoDate).toLocalDateTime();
    }

    // 가장 최근 cancelPayment 추출
    public static Optional<PaymentCancelResponse.CancelDetail> getLatestCancelPaymentByDate(
            final List<PaymentCancelResponse.CancelDetail> cancels) {
        return cancels.stream()
                .filter(cancel -> cancel.canceledAt() != null)
                .max(Comparator.comparing(cancel -> parseISO8601DateToLocalDateTime(cancel.canceledAt())));
    }
}
