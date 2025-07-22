package com.permitseoul.permitserver.domain.payment.api.controller;

import com.permitseoul.permitserver.domain.auth.core.jwt.CookieExtractor;
import com.permitseoul.permitserver.domain.payment.api.exception.NotFoundPaymentException;
import com.permitseoul.permitserver.domain.payment.api.service.PaymentService;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentCancelRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentConfirmRequest;
import com.permitseoul.permitserver.domain.payment.api.dto.PaymentConfirmResponse;
import com.permitseoul.permitserver.domain.reservation.api.exception.ReservationSessionCookieException;
import com.permitseoul.permitserver.global.domain.CookieType;
import com.permitseoul.permitserver.global.resolver.user.UserId;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    //결제 승인 api
    @PostMapping("/confirm")
    public ResponseEntity<BaseResponse<?>> getConfirmToPayment(
            @UserId final Long userId,
            @RequestBody @Valid final PaymentConfirmRequest paymentConfirmRequest,
            final HttpServletRequest request
    ) {
        String reservationSessionKey;
        try {
            reservationSessionKey = CookieExtractor.extractCookie(request, CookieType.RESERVATION_SESSION).getValue();
        } catch (ReservationSessionCookieException e) {
            throw new NotFoundPaymentException(ErrorCode.NOT_FOUND_RESERVATION_SESSION_COOKIE);
        }
        final PaymentConfirmResponse paymentConfirmResponse = paymentService.getPaymentConfirm(
                userId,
                paymentConfirmRequest.orderId(),
                paymentConfirmRequest.paymentKey(),
                paymentConfirmRequest.totalAmount(),
                reservationSessionKey
        );
        return ApiResponseUtil.success(SuccessCode.OK, paymentConfirmResponse);
    }

    //결제 취소 api
    @PostMapping("/cancel")
    public ResponseEntity<BaseResponse<?>> cancelPayment(
            @UserId final Long userId,
            @RequestBody @Valid final PaymentCancelRequest paymentCancelRequest
    ) {
        paymentService.cancelPayment(userId, paymentCancelRequest.orderId());
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
