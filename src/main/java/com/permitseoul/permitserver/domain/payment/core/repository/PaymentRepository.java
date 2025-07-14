package com.permitseoul.permitserver.domain.payment.core.repository;

import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByOrderId(final String orderId);
}
