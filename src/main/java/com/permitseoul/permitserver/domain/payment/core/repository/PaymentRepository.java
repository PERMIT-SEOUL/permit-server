package com.permitseoul.permitserver.domain.payment.core.repository;

import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByOrderId(final String orderId);
    List<PaymentEntity> findByOrderIdIn(final Set<String> orderIds);
}
