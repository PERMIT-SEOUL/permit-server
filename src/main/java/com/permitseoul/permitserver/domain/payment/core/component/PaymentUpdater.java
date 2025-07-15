package com.permitseoul.permitserver.domain.payment.core.component;

import com.permitseoul.permitserver.domain.payment.core.domain.PaymentStatus;
import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentUpdater {

    public void updatePaymentStatus(final PaymentEntity paymentEntity, final PaymentStatus paymentStatus) {
        paymentEntity.updatePaymentStatus(paymentStatus);
    }
}
