package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.global.exception.TimeFormatException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TimeFormatterUtil 테스트")
class TimeFormatterUtilTest {

    @Nested
    @DisplayName("formatEventTime 메서드")
    class FormatEventTime {

        @Test
        @DisplayName("정상적인 시작/종료 시간을 '시작-종료' 형식으로 포맷팅한다")
        void formatsNormalStartAndEndTime() {
            // given
            final LocalDateTime start = LocalDateTime.of(2026, 1, 19, 17, 0);
            final LocalDateTime end = LocalDateTime.of(2026, 1, 19, 19, 0);

            // when
            final String result = TimeFormatterUtil.formatEventTime(start, end);

            // then
            assertThat(result).isEqualTo("17:00-19:00");
        }

        @Test
        @DisplayName("자정을 포함하는 시간을 올바르게 포맷팅한다")
        void formatsTimesAroundMidnight() {
            // given
            final LocalDateTime start = LocalDateTime.of(2026, 1, 19, 23, 30);
            final LocalDateTime end = LocalDateTime.of(2026, 1, 20, 0, 30);

            // when
            final String result = TimeFormatterUtil.formatEventTime(start, end);

            // then
            assertThat(result).isEqualTo("23:30-00:30");
        }

        @Test
        @DisplayName("같은 시간이면 동일한 시간 두 번을 포맷팅한다")
        void formatsSameStartAndEndTime() {
            // given
            final LocalDateTime sameTime = LocalDateTime.of(2026, 1, 19, 14, 0);

            // when
            final String result = TimeFormatterUtil.formatEventTime(sameTime, sameTime);

            // then
            assertThat(result).isEqualTo("14:00-14:00");
        }

        @Test
        @DisplayName("분이 한 자리수여도 두 자리로 표시한다")
        void formatsSingleDigitMinutes() {
            // given
            final LocalDateTime start = LocalDateTime.of(2026, 1, 19, 9, 5);
            final LocalDateTime end = LocalDateTime.of(2026, 1, 19, 10, 0);

            // when
            final String result = TimeFormatterUtil.formatEventTime(start, end);

            // then
            assertThat(result).isEqualTo("09:05-10:00");
        }

        @Test
        @DisplayName("startDateTime이 null이면 TimeFormatException을 던지고 ErrorCode는 INTERNAL_TIME_FORMAT_ERROR이다")
        void throwsExceptionWhenStartDateTimeIsNull() {
            // given
            final LocalDateTime end = LocalDateTime.of(2026, 1, 19, 19, 0);

            // when & then
            assertThatThrownBy(() -> TimeFormatterUtil.formatEventTime(null, end))
                    .isInstanceOf(TimeFormatException.class)
                    .satisfies(exception -> {
                        final TimeFormatException ex = (TimeFormatException) exception;
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_TIME_FORMAT_ERROR);
                    });
        }

        @Test
        @DisplayName("endDateTime이 null이면 TimeFormatException을 던지고 ErrorCode는 INTERNAL_TIME_FORMAT_ERROR이다")
        void throwsExceptionWhenEndDateTimeIsNull() {
            // given
            final LocalDateTime start = LocalDateTime.of(2026, 1, 19, 17, 0);

            // when & then
            assertThatThrownBy(() -> TimeFormatterUtil.formatEventTime(start, null))
                    .isInstanceOf(TimeFormatException.class)
                    .satisfies(exception -> {
                        final TimeFormatException ex = (TimeFormatException) exception;
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_TIME_FORMAT_ERROR);
                    });
        }

        @Test
        @DisplayName("startDateTime과 endDateTime 모두 null이면 TimeFormatException을 던지고 ErrorCode는 INTERNAL_TIME_FORMAT_ERROR이다")
        void throwsExceptionWhenBothAreNull() {
            assertThatThrownBy(() -> TimeFormatterUtil.formatEventTime(null, null))
                    .isInstanceOf(TimeFormatException.class)
                    .satisfies(exception -> {
                        final TimeFormatException ex = (TimeFormatException) exception;
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_TIME_FORMAT_ERROR);
                    });
        }
    }
}
