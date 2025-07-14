package com.permitseoul.permitserver.domain.payment.api.client;

import com.permitseoul.permitserver.domain.payment.api.dto.PaymentRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentResponse;
import com.permitseoul.permitserver.domain.reservation.api.dto.PaymentCancelRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "tossPaymentClient", url = "${payment.toss.base-url}")
public interface TossPaymentClient {

    @PostMapping(value = "/v1/payments/confirm")
    PaymentResponse purchaseConfirm(@RequestHeader(HttpHeaders.AUTHORIZATION) String basicAuth,
                                          @RequestBody PaymentRequest paymentRequest);

    @PostMapping(value = "/v1/payments/{paymentKey}/cancel")
    PaymentResponse cancelPayment(@RequestHeader(HttpHeaders.AUTHORIZATION) String basicAuth,
                                    @PathVariable final String paymentKey,
                                    @RequestBody PaymentCancelRequest paymentRequest);

}

