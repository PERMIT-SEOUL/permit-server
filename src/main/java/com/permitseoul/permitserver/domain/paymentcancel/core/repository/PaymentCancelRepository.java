package com.permitseoul.permitserver.domain.paymentcancel.core.repository;

import com.permitseoul.permitserver.domain.paymentcancel.core.domain.entity.PaymentCancelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCancelRepository extends JpaRepository<PaymentCancelEntity, Long> {
}
