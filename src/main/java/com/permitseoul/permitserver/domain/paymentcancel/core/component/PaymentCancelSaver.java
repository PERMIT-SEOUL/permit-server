package com.permitseoul.permitserver.domain.paymentcancel.core.component;

import com.permitseoul.permitserver.domain.paymentcancel.core.domain.entity.PaymentCancelEntity;
import com.permitseoul.permitserver.domain.paymentcancel.core.repository.PaymentCancelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentCancelSaver {
    private final PaymentCancelRepository paymentCancelRepository;

    public void savePaymentCancel(final long paymentId,
                                  final BigDecimal cancelAmount,
                                  final String transactionKey,
                                  final LocalDateTime canceledAt) {
        paymentCancelRepository.save(PaymentCancelEntity.create(paymentId, cancelAmount, transactionKey, canceledAt));

    }
}
