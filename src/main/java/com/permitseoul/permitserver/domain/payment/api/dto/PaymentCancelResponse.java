package com.permitseoul.permitserver.domain.payment.api.dto;


import java.math.BigDecimal;
import java.util.List;

public record PaymentCancelResponse(
        List<CancelDetail> cancels
) {
    public record CancelDetail(
            String cancelReason,
            BigDecimal cancelAmount,
            String canceledAt,
            String transactionKey
    ) { }
}
