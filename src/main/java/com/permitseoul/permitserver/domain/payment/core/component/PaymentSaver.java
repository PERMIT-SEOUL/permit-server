package com.permitseoul.permitserver.domain.payment.core.component;

import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import com.permitseoul.permitserver.domain.payment.core.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentSaver {
    private final PaymentRepository paymentRepository;

    public Payment savePayment(final long reservationId,
                               final String orderId,
                               final long eventId,
                               String paymentKey,
                               BigDecimal totalAmount,
                               String currency) {
        final PaymentEntity paymentEntity = paymentRepository.save(new PaymentEntity(reservationId, orderId, eventId, paymentKey, totalAmount, currency));
        return Payment.fromEntity(paymentEntity);
    }

}
