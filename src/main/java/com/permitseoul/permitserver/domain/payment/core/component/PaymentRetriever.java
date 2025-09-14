package com.permitseoul.permitserver.domain.payment.core.component;

import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import com.permitseoul.permitserver.domain.payment.core.exception.PaymentNotFoundException;
import com.permitseoul.permitserver.domain.payment.core.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class PaymentRetriever {
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Payment findPaymentByOrderId(final String orderId) {
        final PaymentEntity paymentEntity = paymentRepository.findByOrderId(orderId).orElseThrow(PaymentNotFoundException::new);
        return Payment.fromEntity(paymentEntity);
    }

    @Transactional(readOnly = true)
    public PaymentEntity findPaymentEntityByOrderId(final String orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(PaymentNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Payment> findPaymentByOrderIdIn(final Set<String> orderIds) {
        final List<PaymentEntity> paymentEntities = paymentRepository.findByOrderIdIn(orderIds);
        if (ObjectUtils.isEmpty(paymentEntities)) {
            throw new PaymentNotFoundException();
        }
        return paymentEntities.stream().map(Payment::fromEntity).toList();
    }

}
