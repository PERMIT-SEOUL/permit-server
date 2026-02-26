package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.domain.payment.api.dto.PaymentCancelResponse;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LocalDateTimeFormatterUtil 테스트")
class LocalDateTimeFormatterUtilTest {

    @Nested
    @DisplayName("formatStartEndDate 메서드")
    class FormatStartEndDate {

        @Test
        @DisplayName("시작일과 종료일이 같으면 'Jan 19 (Mon), 2026' 형식으로 반환한다")
        void formatsSameDateCorrectly() {
            // given
            final LocalDateTime start = LocalDateTime.of(2026, 1, 19, 17, 0);
            final LocalDateTime end = LocalDateTime.of(2026, 1, 19, 19, 0);

            // when
            final String result = LocalDateTimeFormatterUtil.formatStartEndDate(start, end);

            // then
            assertThat(result).isEqualTo("Jan 19 (Mon), 2026");
        }

        @Test
        @DisplayName("같은 연도 내 다른 날짜면 'Jan 26 (Mon) – Jan 29 (Thu), 2026' 형식으로 반환한다")
        void formatsSameYearDifferentDates() {
            // given
            final LocalDateTime start = LocalDateTime.of(2026, 1, 26, 17, 0);
            final LocalDateTime end = LocalDateTime.of(2026, 1, 29, 19, 0);

            // when
            final String result = LocalDateTimeFormatterUtil.formatStartEndDate(start, end);

            // then
            assertThat(result).isEqualTo("Jan 26 (Mon) – Jan 29 (Thu), 2026");
        }

        @Test
        @DisplayName("같은 연도 내 다른 월이면 'Jan 26 (Mon) – Feb 10 (Tue), 2026' 형식으로 반환한다")
        void formatsSameYearDifferentMonths() {
            // given
            final LocalDateTime start = LocalDateTime.of(2026, 1, 26, 17, 0);
            final LocalDateTime end = LocalDateTime.of(2026, 2, 10, 19, 0);

            // when
            final String result = LocalDateTimeFormatterUtil.formatStartEndDate(start, end);

            // then
            assertThat(result).isEqualTo("Jan 26 (Mon) – Feb 10 (Tue), 2026");
        }

        @Test
        @DisplayName("연도가 다르면 'Dec 25 (Thu), 2025 – Jan 5 (Mon), 2026' 형식으로 반환한다")
        void formatsDifferentYears() {
            // given
            final LocalDateTime start = LocalDateTime.of(2025, 12, 25, 17, 0);
            final LocalDateTime end = LocalDateTime.of(2026, 1, 5, 19, 0);

            // when
            final String result = LocalDateTimeFormatterUtil.formatStartEndDate(start, end);

            // then
            assertThat(result).isEqualTo("Dec 25 (Thu), 2025 – Jan 5 (Mon), 2026");
        }

        @Test
        @DisplayName("startDate가 null이면 IllegalArgumentException을 던진다")
        void throwsExceptionWhenStartDateIsNull() {
            // given
            final LocalDateTime end = LocalDateTime.of(2026, 1, 19, 19, 0);

            // when & then
            assertThatThrownBy(() -> LocalDateTimeFormatterUtil.formatStartEndDate(null, end))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("startDate가 null");
        }

        @Test
        @DisplayName("endDate가 null이면 IllegalArgumentException을 던진다")
        void throwsExceptionWhenEndDateIsNull() {
            // given
            final LocalDateTime start = LocalDateTime.of(2026, 1, 19, 17, 0);

            // when & then
            assertThatThrownBy(() -> LocalDateTimeFormatterUtil.formatStartEndDate(start, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("endDate가 null");
        }
    }

    @Nested
    @DisplayName("formatDayWithDate 메서드")
    class FormatDayWithDate {

        @Test
        @DisplayName("2026-01-04(일) → 'Sun, 04' 형식으로 반환한다")
        void formatsDateCorrectly() {
            // given
            final LocalDateTime dateTime = LocalDateTime.of(2026, 1, 4, 10, 0);

            // when
            final String result = LocalDateTimeFormatterUtil.formatDayWithDate(dateTime);

            // then
            assertThat(result).isEqualTo("Sun, 04");
        }
    }

    @Nested
    @DisplayName("formatYearMonth 메서드")
    class FormatYearMonth {

        @Test
        @DisplayName("2025년 8월 → '2025.08' 형식으로 반환한다")
        void formatsYearMonthCorrectly() {
            // given
            final LocalDateTime dateTime = LocalDateTime.of(2025, 8, 15, 10, 0);

            // when
            final String result = LocalDateTimeFormatterUtil.formatYearMonth(dateTime);

            // then
            assertThat(result).isEqualTo("2025.08");
        }
    }

    @Nested
    @DisplayName("formatyyyyMMdd 메서드")
    class FormatYyyyMMdd {

        @Test
        @DisplayName("'2025-08-15' 형식으로 반환한다")
        void formatsDateCorrectly() {
            // given
            final LocalDateTime dateTime = LocalDateTime.of(2025, 8, 15, 10, 0);

            // when
            final String result = LocalDateTimeFormatterUtil.formatyyyyMMdd(dateTime);

            // then
            assertThat(result).isEqualTo("2025-08-15");
        }
    }

    @Nested
    @DisplayName("formatHHmm 메서드")
    class FormatHHmm {

        @Test
        @DisplayName("'17:30' 형식으로 반환한다")
        void formatsTimeCorrectly() {
            // given
            final LocalDateTime dateTime = LocalDateTime.of(2026, 1, 19, 17, 30);

            // when
            final String result = LocalDateTimeFormatterUtil.formatHHmm(dateTime);

            // then
            assertThat(result).isEqualTo("17:30");
        }
    }

    @Nested
    @DisplayName("combineDateAndTime 메서드")
    class CombineDateAndTime {

        @Test
        @DisplayName("날짜와 시간을 결합하여 LocalDateTime을 반환한다")
        void combinesDateAndTime() {
            // given
            final LocalDate date = LocalDate.of(2026, 1, 19);
            final LocalTime time = LocalTime.of(17, 30);

            // when
            final LocalDateTime result = LocalDateTimeFormatterUtil.combineDateAndTime(date, time);

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2026, 1, 19, 17, 30));
        }

        @Test
        @DisplayName("date가 null이면 NullPointerException을 던진다")
        void throwsExceptionWhenDateIsNull() {
            // given
            final LocalTime time = LocalTime.of(17, 30);

            // when & then
            assertThatThrownBy(() -> LocalDateTimeFormatterUtil.combineDateAndTime(null, time))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("date가 null");
        }

        @Test
        @DisplayName("time이 null이면 NullPointerException을 던진다")
        void throwsExceptionWhenTimeIsNull() {
            // given
            final LocalDate date = LocalDate.of(2026, 1, 19);

            // when & then
            assertThatThrownBy(() -> LocalDateTimeFormatterUtil.combineDateAndTime(date, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("time이 null");
        }
    }

    @Nested
    @DisplayName("combineDateAndTimeForUpdate 메서드")
    class CombineDateAndTimeForUpdate {

        @Test
        @DisplayName("date와 time 모두 null이면 originalDateTime을 그대로 반환한다")
        void returnsOriginalWhenBothNull() {
            // given
            final LocalDateTime original = LocalDateTime.of(2026, 1, 19, 17, 30);

            // when
            final LocalDateTime result = LocalDateTimeFormatterUtil.combineDateAndTimeForUpdate(null, null, original);

            // then
            assertThat(result).isEqualTo(original);
        }

        @Test
        @DisplayName("date만 제공되면 originalDateTime의 시간과 새 날짜를 결합한다")
        void usesNewDateWithOriginalTime() {
            // given
            final LocalDate newDate = LocalDate.of(2026, 2, 1);
            final LocalDateTime original = LocalDateTime.of(2026, 1, 19, 17, 30);

            // when
            final LocalDateTime result = LocalDateTimeFormatterUtil.combineDateAndTimeForUpdate(newDate, null,
                    original);

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2026, 2, 1, 17, 30));
        }

        @Test
        @DisplayName("time만 제공되면 originalDateTime의 날짜와 새 시간을 결합한다")
        void usesOriginalDateWithNewTime() {
            // given
            final LocalTime newTime = LocalTime.of(20, 0);
            final LocalDateTime original = LocalDateTime.of(2026, 1, 19, 17, 30);

            // when
            final LocalDateTime result = LocalDateTimeFormatterUtil.combineDateAndTimeForUpdate(null, newTime,
                    original);

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2026, 1, 19, 20, 0));
        }

        @Test
        @DisplayName("date와 time 모두 제공되면 새 날짜와 새 시간을 결합한다")
        void usesBothNewDateAndTime() {
            // given
            final LocalDate newDate = LocalDate.of(2026, 3, 1);
            final LocalTime newTime = LocalTime.of(9, 0);
            final LocalDateTime original = LocalDateTime.of(2026, 1, 19, 17, 30);

            // when
            final LocalDateTime result = LocalDateTimeFormatterUtil.combineDateAndTimeForUpdate(newDate, newTime,
                    original);

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2026, 3, 1, 9, 0));
        }

        @Test
        @DisplayName("originalDateTime이 null이면 DateFormatException을 던진다")
        void throwsExceptionWhenOriginalIsNull() {
            assertThatThrownBy(() -> LocalDateTimeFormatterUtil.combineDateAndTimeForUpdate(
                    LocalDate.of(2026, 1, 19), LocalTime.of(17, 0), null))
                    .isInstanceOf(DateFormatException.class);
        }
    }

    @Nested
    @DisplayName("parseISO8601DateToLocalDateTime 메서드")
    class ParseISO8601DateToLocalDateTime {

        @Test
        @DisplayName("ISO 8601 형식의 문자열을 LocalDateTime으로 변환한다")
        void parsesValidISODate() {
            // given
            final String isoDate = "2026-01-19T17:30:00+09:00";

            // when
            final LocalDateTime result = LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime(isoDate);

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2026, 1, 19, 17, 30, 0));
        }

        @Test
        @DisplayName("null이 입력되면 DateFormatException을 던진다")
        void throwsExceptionWhenNull() {
            assertThatThrownBy(() -> LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime(null))
                    .isInstanceOf(DateFormatException.class);
        }

        @Test
        @DisplayName("빈 문자열이 입력되면 DateFormatException을 던진다")
        void throwsExceptionWhenEmpty() {
            assertThatThrownBy(() -> LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime(""))
                    .isInstanceOf(DateFormatException.class);
        }

        @Test
        @DisplayName("공백 문자열이 입력되면 DateFormatException을 던진다")
        void throwsExceptionWhenBlank() {
            assertThatThrownBy(() -> LocalDateTimeFormatterUtil.parseISO8601DateToLocalDateTime("   "))
                    .isInstanceOf(DateFormatException.class);
        }
    }

    @Nested
    @DisplayName("getLatestCancelPaymentByDate 메서드")
    class GetLatestCancelPaymentByDate {

        @Test
        @DisplayName("가장 최근 취소 내역을 반환한다")
        void returnsLatestCancelDetail() {
            // given
            final PaymentCancelResponse.CancelDetail older = new PaymentCancelResponse.CancelDetail(
                    "사용자 요청", new java.math.BigDecimal("30000"), "2026-01-10T10:00:00+09:00", "txKey1");
            final PaymentCancelResponse.CancelDetail newer = new PaymentCancelResponse.CancelDetail(
                    "사용자 요청", new java.math.BigDecimal("50000"), "2026-01-15T10:00:00+09:00", "txKey2");

            // when
            final Optional<PaymentCancelResponse.CancelDetail> result = LocalDateTimeFormatterUtil
                    .getLatestCancelPaymentByDate(List.of(older, newer));

            // then
            assertThat(result).isPresent();
            assertThat(result.get().transactionKey()).isEqualTo("txKey2");
        }

        @Test
        @DisplayName("canceledAt이 null인 항목은 필터링한다")
        void filtersOutNullCanceledAt() {
            // given
            final PaymentCancelResponse.CancelDetail withDate = new PaymentCancelResponse.CancelDetail(
                    "사용자 요청", new java.math.BigDecimal("30000"), "2026-01-10T10:00:00+09:00", "txKey1");
            final PaymentCancelResponse.CancelDetail withoutDate = new PaymentCancelResponse.CancelDetail(
                    "사용자 요청", new java.math.BigDecimal("50000"), null, "txKey2");

            // when
            final Optional<PaymentCancelResponse.CancelDetail> result = LocalDateTimeFormatterUtil
                    .getLatestCancelPaymentByDate(List.of(withDate, withoutDate));

            // then
            assertThat(result).isPresent();
            assertThat(result.get().transactionKey()).isEqualTo("txKey1");
        }

        @Test
        @DisplayName("빈 리스트이면 빈 Optional을 반환한다")
        void returnsEmptyOptionalWhenListIsEmpty() {
            // when
            final Optional<PaymentCancelResponse.CancelDetail> result = LocalDateTimeFormatterUtil
                    .getLatestCancelPaymentByDate(Collections.emptyList());

            // then
            assertThat(result).isEmpty();
        }
    }
}
