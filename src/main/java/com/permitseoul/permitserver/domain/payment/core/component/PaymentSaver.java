package com.permitseoul.permitserver.domain.payment.core.component;

import com.permitseoul.permitserver.domain.payment.core.domain.Currency;
import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import com.permitseoul.permitserver.domain.payment.core.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentSaver {
    private final PaymentRepository paymentRepository;

    public Payment savePayment(final long reservationId,
                               final String orderId,
                               final long eventId,
                               String paymentKey,
                               BigDecimal totalAmount,
                               Currency currency,
                               LocalDateTime requestedAt,
                               LocalDateTime approvedAt) {
        final PaymentEntity paymentEntity = paymentRepository.save(
                PaymentEntity.create(reservationId, orderId, eventId, paymentKey, totalAmount, currency, requestedAt, approvedAt)
        );
        return Payment.fromEntity(paymentEntity);
    }

}
