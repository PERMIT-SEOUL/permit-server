package com.permitseoul.permitserver.domain.payment.core.component;

import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import com.permitseoul.permitserver.domain.payment.core.exception.PaymentNotFoundException;
import com.permitseoul.permitserver.domain.payment.core.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PaymentRetriever {
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Payment findPaymentByOrderId(final String orderId) {
        final PaymentEntity paymentEntity = paymentRepository.findByOrderId(orderId).orElseThrow(PaymentNotFoundException::new);
        return Payment.fromEntity(paymentEntity);
    }

}
