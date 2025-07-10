package com.permitseoul.permitserver.domain.reservation.api.controller;

import com.permitseoul.permitserver.domain.reservation.api.dto.PaymentReadyRequest;
import com.permitseoul.permitserver.domain.reservation.api.dto.PaymentReadyResponse;
import com.permitseoul.permitserver.domain.reservation.api.service.ReservationService;
import com.permitseoul.permitserver.global.resolver.user.UserId;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    //결제 요청 준비
    @PostMapping("/ready")
    public ResponseEntity<BaseResponse<?>> getReadyToPayment(
            @RequestBody @Valid final PaymentReadyRequest paymentReadyRequest,
            @UserId final Long userId
    ) {
        final PaymentReadyResponse response = reservationService.getPaymentReady(
                userId,
                paymentReadyRequest.eventId(),
                paymentReadyRequest.couponCode(),
                paymentReadyRequest.totalAmount(),
                paymentReadyRequest.orderId(),
                paymentReadyRequest.ticketTypeInfos()
        );
        return ApiResponseUtil.success(SuccessCode.OK, response);
    }
}
