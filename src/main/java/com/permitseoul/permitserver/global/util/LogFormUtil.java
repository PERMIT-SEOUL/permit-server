package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.global.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import net.logstash.logback.argument.StructuredArgument;

import java.math.BigDecimal;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@UtilityClass
public final class LogFormUtil {
    private final static String MASKING_STAR = "*";
    private static final int PAYMENT_KEY_TINY_MAX = 2;
    private static final int PAYMENT_KEY_SMALL_MAX = 4;
    private static final int PAYMENT_KEY_SHORT_MAX = 8;
    private static final int PAYMENT_KEY_MIN_EDGE_LENGTH = 1;
    private static final int PAYMENT_KEY_SHORT_PREFIX = 2;
    private static final int PAYMENT_KEY_SHORT_SUFFIX = 2;
    private static final int PAYMENT_KEY_LONG_PREFIX = 4;
    private static final int PAYMENT_KEY_LONG_SUFFIX = 5;

    public static StructuredArgument[] paymentLog(
            final long userId,
            final String orderId,
            final String paymentKey,
            final long reservationId,
            final BigDecimal totalAmount
    ) {
        return new StructuredArgument[] {
                keyValue(Constants.USER_ID, userId),
                keyValue(Constants.ORDER_ID, orderId),
                keyValue(Constants.PAYMENT_KEY, maskPaymentKey(paymentKey)),
                keyValue(Constants.RESERVATION_ID, reservationId),
                keyValue(Constants.TOTAL_AMOUNT, totalAmount)
        };
    }

    public static String maskPaymentKey(final String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            return paymentKey;
        }
        final int length = paymentKey.length();
        if (length <= PAYMENT_KEY_TINY_MAX) {
            return paymentKey.substring(0, PAYMENT_KEY_MIN_EDGE_LENGTH)
                    + MASKING_STAR.repeat(length - PAYMENT_KEY_MIN_EDGE_LENGTH);
        }
        if (length <= PAYMENT_KEY_SMALL_MAX) {
            final String prefix = paymentKey.substring(0, PAYMENT_KEY_MIN_EDGE_LENGTH);
            final String suffix = paymentKey.substring(length - PAYMENT_KEY_MIN_EDGE_LENGTH);
            return prefix
                    + MASKING_STAR.repeat(length - (PAYMENT_KEY_MIN_EDGE_LENGTH * 2))
                    + suffix;
        }

        final boolean isShort = length <= PAYMENT_KEY_SHORT_MAX;
        final int suffixLength = isShort ? PAYMENT_KEY_SHORT_SUFFIX : PAYMENT_KEY_LONG_SUFFIX;
        final int prefixLength = Math.min(isShort ? PAYMENT_KEY_SHORT_PREFIX : PAYMENT_KEY_LONG_PREFIX,
                length - suffixLength - PAYMENT_KEY_MIN_EDGE_LENGTH);

        final String prefix = paymentKey.substring(0, prefixLength);
        final String suffix = paymentKey.substring(length - suffixLength);
        final int maskLength = length - prefixLength - suffixLength;
        return prefix + MASKING_STAR.repeat(maskLength) + suffix;
    }
}
