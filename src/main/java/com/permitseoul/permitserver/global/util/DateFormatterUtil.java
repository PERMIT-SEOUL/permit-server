package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.domain.payment.api.dto.PaymentCancelResponse;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateFormatterUtil {
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E", Locale.ENGLISH);   // Sun
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd", Locale.ENGLISH); // 25
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH); // May 2025
    private static final DateTimeFormatter DAY_DD_FORMATTER = DateTimeFormatter.ofPattern("E, dd", Locale.ENGLISH); // Fri, 04
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM", Locale.ENGLISH); // 2025.09
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd"); // 2025.08.15
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm"); //17:30
    private static final String COMMA_AND_SPACE = ", ";
    private static final String SPACE = " ";
    private static final String DASH = "-";

    public static String formatEventDate(final LocalDateTime startDate, final LocalDateTime endDate) {
        final StringBuilder sb = new StringBuilder();
        // 시작날짜와 종료날짜가 같을 때, "Sun, 25 May 2025" 형식으로 포맷팅
        if (startDate.toLocalDate().equals(endDate.toLocalDate())) {
            sb.append(startDate.format(DAY_FORMATTER))
                    .append(COMMA_AND_SPACE)
                    .append(startDate.format(DATE_FORMATTER))
                    .append(SPACE)
                    .append(startDate.format(MONTH_YEAR_FORMATTER));
        } else { // 시작날짜와 종료날짜가 다를 때, "Sun–Mon, 25–26 May 2025" 형식으로 포맷팅
            sb.append(startDate.format(DAY_FORMATTER))
                    .append(DASH)
                    .append(endDate.format(DAY_FORMATTER))
                    .append(COMMA_AND_SPACE)
                    .append(startDate.format(DATE_FORMATTER))
                    .append(DASH)
                    .append(endDate.format(DATE_FORMATTER))
                    .append(SPACE)
                    .append(endDate.format(MONTH_YEAR_FORMATTER));
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
                Objects.requireNonNull(time, "time이 null입니다.")
        );
    }

    // update할 때, 결합
    public static LocalDateTime combineDateAndTimeForUpdate(final LocalDate date, final LocalTime time, final LocalDateTime originalDateTime) {
        if (date == null && time == null) {
            return originalDateTime;
        }
        final LocalDate localDate = date == null ? originalDateTime.toLocalDate() : date;
        final LocalTime localTime = time == null ? originalDateTime.toLocalTime() : time;
        return LocalDateTime.of(localDate, localTime);
    }

    // 토스에서 주는 날짜 형식 ISO 8601을 LocalDateTime로 변환
    public static LocalDateTime parseTossDateToLocalDateTime(final String isoDate) {
        if (isoDate == null || isoDate.isBlank()) {
            throw new DateFormatException();
        }
        return OffsetDateTime.parse(isoDate).toLocalDateTime();
    }

    // 가장 최근 cancelPayment 추출
    public static Optional<PaymentCancelResponse.CancelDetail> getLatestCancelPaymentByDate(final List<PaymentCancelResponse.CancelDetail> cancels) {
        return cancels.stream()
                .filter(cancel -> cancel.canceledAt() != null)
                .max(Comparator.comparing(cancel -> parseTossDateToLocalDateTime(cancel.canceledAt())));
    }
}
