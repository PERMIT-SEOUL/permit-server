package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.global.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.logstash.logback.argument.StructuredArgument;

import java.math.BigDecimal;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogFormUtil {

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
                keyValue(Constants.PAYMENT_KEY, paymentKey),
                keyValue(Constants.RESERVATION_ID, reservationId),
                keyValue(Constants.TOTAL_AMOUNT, totalAmount)
        };
    }
}
