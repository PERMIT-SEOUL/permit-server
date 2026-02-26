package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.global.exception.PriceFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PriceFormatterUtil 테스트")
class PriceFormatterUtilTest {

    @Nested
    @DisplayName("formatPrice 메서드")
    class FormatPrice {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "60000, '60,000'",
                "0, '0'",
                "1000000, '1,000,000'",
                "999, '999'",
                "1000, '1,000'",
                "123456789, '123,456,789'"
        })
        @DisplayName("다양한 가격을 올바르게 포맷팅한다")
        void formatsVariousPrices(final String input, final String expected) {
            // given
            final BigDecimal price = new BigDecimal(input);

            // when
            final String result = PriceFormatterUtil.formatPrice(price);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("null이 입력되면 '-'을 반환한다")
        void returnsDashWhenPriceIsNull() {
            // given & when
            final String result = PriceFormatterUtil.formatPrice(null);

            // then
            assertThat(result).isEqualTo("-");
        }

        @Test
        @DisplayName("소수점이 있는 가격은 정수부만 콤마를 포함한다")
        void formatsDecimalPrice() {
            // given
            final BigDecimal price = new BigDecimal("60000.50");

            // when
            final String result = PriceFormatterUtil.formatPrice(price);

            // then
            assertThat(result).contains("60,000");
        }
    }

    @Nested
    @DisplayName("formatRoundPrice 메서드")
    class FormatRoundPrice {

        @Test
        @DisplayName("가격이 하나뿐이면 단일 가격을 반환한다")
        void returnsSinglePriceWhenOnlyOnePrice() {
            // given
            final List<BigDecimal> prices = List.of(new BigDecimal("50000"));

            // when
            final String result = PriceFormatterUtil.formatRoundPrice(prices);

            // then
            assertThat(result).isEqualTo("50,000");
        }

        @Test
        @DisplayName("모든 가격이 동일하면 단일 가격을 반환한다")
        void returnsSinglePriceWhenAllPricesAreSame() {
            // given
            final List<BigDecimal> prices = List.of(
                    new BigDecimal("30000"),
                    new BigDecimal("30000"),
                    new BigDecimal("30000"));

            // when
            final String result = PriceFormatterUtil.formatRoundPrice(prices);

            // then
            assertThat(result).isEqualTo("30,000");
        }

        @Test
        @DisplayName("가격이 다르면 '최저가 ~ 최고가' 형식으로 반환한다")
        void returnsRangeWhenPricesDiffer() {
            // given
            final List<BigDecimal> prices = List.of(
                    new BigDecimal("30000"),
                    new BigDecimal("50000"),
                    new BigDecimal("80000"));

            // when
            final String result = PriceFormatterUtil.formatRoundPrice(prices);

            // then
            assertThat(result).isEqualTo("30,000 ~ 80,000");
        }

        @Test
        @DisplayName("두 개의 다른 가격이면 '최저가 ~ 최고가' 형식으로 반환한다")
        void returnsRangeWithTwoDifferentPrices() {
            // given
            final List<BigDecimal> prices = List.of(
                    new BigDecimal("10000"),
                    new BigDecimal("50000"));

            // when
            final String result = PriceFormatterUtil.formatRoundPrice(prices);

            // then
            assertThat(result).isEqualTo("10,000 ~ 50,000");
        }

        @Test
        @DisplayName("정렬되지 않은 가격 리스트도 올바르게 정렬하여 반환한다")
        void sortsUnsortedPricesCorrectly() {
            // given
            final List<BigDecimal> prices = List.of(
                    new BigDecimal("80000"),
                    new BigDecimal("10000"),
                    new BigDecimal("50000"));

            // when
            final String result = PriceFormatterUtil.formatRoundPrice(prices);

            // then
            assertThat(result).isEqualTo("10,000 ~ 80,000");
        }

        @Test
        @DisplayName("prices가 null이면 PriceFormatException을 던진다")
        void throwsExceptionWhenPricesIsNull() {
            assertThatThrownBy(() -> PriceFormatterUtil.formatRoundPrice(null))
                    .isInstanceOf(PriceFormatException.class);
        }

        @Test
        @DisplayName("prices가 빈 리스트이면 PriceFormatException을 던진다")
        void throwsExceptionWhenPricesIsEmpty() {
            assertThatThrownBy(() -> PriceFormatterUtil.formatRoundPrice(Collections.emptyList()))
                    .isInstanceOf(PriceFormatException.class);
        }
    }
}
