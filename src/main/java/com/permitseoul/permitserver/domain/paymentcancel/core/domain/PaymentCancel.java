package com.permitseoul.permitserver.domain.paymentcancel.core.domain;

import com.permitseoul.permitserver.domain.paymentcancel.core.domain.entity.PaymentCancelEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class PaymentCancel {
    private final Long paymentCancelsId;
    private final long paymentsId;
    private final BigDecimal cancelAmount;
    private final String transactionKey;
    private final LocalDateTime canceledAt;

    public static PaymentCancel fromEntity(final PaymentCancelEntity entity) {
        return new PaymentCancel(
                entity.getPaymentCancelId(),
                entity.getPaymentId(),
                entity.getCancelAmount(),
                entity.getTransactionKey(),
                entity.getCanceledAt()
        );
    }

}
