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
                keyValue(Constants.PAYMENT_KEY, maskMiddle(paymentKey,5)),
                keyValue(Constants.RESERVATION_ID, reservationId),
                keyValue(Constants.TOTAL_AMOUNT, totalAmount)
        };
    }

    public static String maskMiddle(final String input, final int visible) {
        if (input == null || input.length() <= visible * 2) {
            return input;
        }
        final String prefix = input.substring(0, visible);
        final String suffix = input.substring(input.length() - visible);
        final String masked = MASKING_STAR.repeat(input.length() - (visible * 2));

        return prefix + masked + suffix;
    }
}
