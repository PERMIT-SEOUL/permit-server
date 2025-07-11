package com.permitseoul.permitserver.domain.payment.core.repository;

import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
