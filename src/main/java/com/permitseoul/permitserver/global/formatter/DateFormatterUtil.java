package com.permitseoul.permitserver.global.formatter;

import com.permitseoul.permitserver.domain.payment.api.dto.PaymentCancelResponse;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public abstract class DateFormatterUtil {
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E", Locale.ENGLISH);         //Sun
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd", Locale.ENGLISH);       //25
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH); //May 2025

    public static String formatEventDate(final LocalDateTime startDate, final LocalDateTime endDate) {
        final StringBuilder sb = new StringBuilder();
        // 시작날짜와 종료날짜가 같을 때, "Sun, 25 May 2025" 형식으로 포맷팅
        if (startDate.toLocalDate().equals(endDate.toLocalDate())) {
            sb.append(startDate.format(DAY_FORMATTER))
                    .append(", ")
                    .append(startDate.format(DATE_FORMATTER))
                    .append(" ")
                    .append(startDate.format(MONTH_YEAR_FORMATTER));
        } else { // 시작날짜와 종료날짜가 다를 때, "Sun–Mon, 25–26 May 2025" 형식으로 포맷팅
            sb.append(startDate.format(DAY_FORMATTER))
                    .append("–")
                    .append(endDate.format(DAY_FORMATTER))
                    .append(", ")
                    .append(startDate.format(DATE_FORMATTER))
                    .append("–")
                    .append(endDate.format(DATE_FORMATTER))
                    .append(" ")
                    .append(endDate.format(MONTH_YEAR_FORMATTER));
        }
        return sb.toString();
    }

    // 토스에서 주는 날짜 형식 ISO 8601을 LocalDateTime로 변환
    public static LocalDateTime parseDateToLocalDateTime(final String isoDate) {
        if (isoDate == null || isoDate.isBlank()) {
            throw new DateFormatException(ErrorCode.INTERNAL_ISO_DATE_ERROR);
        }
        return OffsetDateTime.parse(isoDate).toLocalDateTime();
    }

    // 가장 최근 cancelPayment 추출
    public static Optional<PaymentCancelResponse.CancelDetail> getLatestCancelPaymentByDate(final List<PaymentCancelResponse.CancelDetail> cancels) {
        return cancels.stream()
                .filter(cancel -> cancel.canceledAt() != null)
                .max(Comparator.comparing(cancel -> parseDateToLocalDateTime(cancel.canceledAt())));
    }
}
