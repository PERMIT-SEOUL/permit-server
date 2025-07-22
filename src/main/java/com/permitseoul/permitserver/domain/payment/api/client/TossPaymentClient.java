package com.permitseoul.permitserver.domain.payment.api.client;

import com.permitseoul.permitserver.domain.payment.api.dto.PaymentCancelResponse;
import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentResponse;
import com.permitseoul.permitserver.domain.payment.api.dto.TossPaymentCancelRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "tossPaymentClient", url = "${payment.toss.base-url}")
public interface TossPaymentClient {

    @PostMapping(value = "/v1/payments/confirm")
    TossPaymentResponse purchaseConfirm(@RequestHeader(HttpHeaders.AUTHORIZATION) String basicAuth,
                                        @RequestBody TossPaymentRequest tossPaymentRequest);

    @PostMapping(value = "/v1/payments/{paymentKey}/cancel")
    PaymentCancelResponse cancelPayment(@RequestHeader(HttpHeaders.AUTHORIZATION) String basicAuth,
                                        @PathVariable("paymentKey") String paymentKey,
                                        @RequestBody TossPaymentCancelRequest paymentRequest);

}

