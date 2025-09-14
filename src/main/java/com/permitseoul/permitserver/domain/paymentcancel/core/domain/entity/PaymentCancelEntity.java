package com.permitseoul.permitserver.domain.paymentcancel.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_cancels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentCancelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_cancels_id")
    private Long paymentCancelId;

    @Column(name = "payments_id", nullable = false)
    private long paymentId;

    @Column(name = "cancel_amount", nullable = false)
    private BigDecimal cancelAmount;

    //결제키(유니크키)
    @Column(name = "transaction_key", nullable = false)
    private String transactionKey;

    @Column(name = "canceled_at", nullable = false)
    private LocalDateTime canceledAt;

    private PaymentCancelEntity(long paymentId, BigDecimal cancelAmount, String transactionKey, LocalDateTime canceledAt) {
        this.paymentId = paymentId;
        this.cancelAmount = cancelAmount;
        this.transactionKey = transactionKey;
        this.canceledAt = canceledAt;
    }

    public static PaymentCancelEntity create(final long paymentsId,
                                             final BigDecimal cancelAmount,
                                             final String transactionKey,
                                             final LocalDateTime canceledAt) {
        return new PaymentCancelEntity(paymentsId, cancelAmount, transactionKey, canceledAt);
    }
}
