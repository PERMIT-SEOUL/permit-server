package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.global.exception.PriceFormatException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@UtilityClass
public final class PriceFormatterUtil {

    private static final String DECIMAL_PATTERN = "#,###";

    // BigDecimal타입 가격을 60,000 형태로 변환
    public static String formatPrice(final BigDecimal price) {
        if (price == null) return "-";
        return new DecimalFormat(DECIMAL_PATTERN).format(price);
    }

    // 한 라운드 내에 티켓타입들의 "최저가 ~ 최고가" or 단일가격(티켓타입 하나일 때) 반환
    public static String formatRoundPrice(final List<BigDecimal> prices) {
        if (prices == null || prices.isEmpty()) {
            throw new PriceFormatException();
        }

        final List<BigDecimal> sorted = prices.stream()
                .sorted()
                .toList();

        final BigDecimal min = sorted.get(0);
        final BigDecimal max = sorted.get(sorted.size() - 1);

        if (min.compareTo(max) == 0) {
            return formatPrice(min);
        }
        return formatPrice(min) + " ~ " + formatPrice(max);
    }
}