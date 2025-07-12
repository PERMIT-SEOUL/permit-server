package com.permitseoul.permitserver.domain.payment.api.client;

import com.permitseoul.permitserver.domain.payment.api.dto.PaymentRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "tossPaymentClient", url = "https://api.tosspayments.com")
public interface TossPaymentClient {

    @PostMapping(value = "/v1/payments/confirm")
    PaymentResponse purchaseConfirm(@RequestHeader(HttpHeaders.AUTHORIZATION) String basicAuth,
                                          @RequestBody PaymentRequest paymentRequest);

}

