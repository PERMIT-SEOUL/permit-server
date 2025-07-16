package com.permitseoul.permitserver.domain.payment.core.component;

import com.permitseoul.permitserver.domain.payment.core.domain.Currency;
import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import com.permitseoul.permitserver.domain.payment.core.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentSaver {
    private final PaymentRepository paymentRepository;

    @Transactional
    public void savePayment(final long reservationId,
                            final String orderId,
                            final long eventId,
                            final String paymentKey,
                            final BigDecimal totalAmount,
                            final Currency currency,
                            final LocalDateTime requestedAt,
                            final LocalDateTime approvedAt) {
        paymentRepository.save(
                PaymentEntity.create(reservationId, orderId, eventId, paymentKey, totalAmount, currency, requestedAt, approvedAt)
        );
    }

}
