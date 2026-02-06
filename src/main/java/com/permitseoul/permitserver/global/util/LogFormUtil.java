package com.permitseoul.permitserver.global.util;

import lombok.experimental.UtilityClass;

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
