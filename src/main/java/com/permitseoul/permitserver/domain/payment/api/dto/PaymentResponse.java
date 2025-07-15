package com.permitseoul.permitserver.domain.payment.api.dto;

import com.permitseoul.permitserver.domain.payment.core.domain.Currency;

import java.math.BigDecimal;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        Currency currency,
        BigDecimal totalAmount,
        String requestedAt,
        String approvedAt
) { }
