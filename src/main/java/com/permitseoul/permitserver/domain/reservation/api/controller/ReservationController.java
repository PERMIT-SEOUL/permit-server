package com.permitseoul.permitserver.domain.reservation.api.controller;

import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoRequest;
import com.permitseoul.permitserver.domain.reservation.api.dto.*;
import com.permitseoul.permitserver.domain.reservation.api.service.ReservationService;
import com.permitseoul.permitserver.global.resolver.user.UserId;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    //예약 생성 api
    @PostMapping("/ready")
    public ResponseEntity<BaseResponse<?>> saveReservation(
            @RequestBody @Valid final ReservationInfoRequest reservationInfoRequest,
            @UserId final Long userId
    ) {
        final String orderId = reservationService.saveReservation(
                userId,
                reservationInfoRequest.eventId(),
                reservationInfoRequest.couponCode(),
                reservationInfoRequest.totalAmount(),
                reservationInfoRequest.orderId(),
                reservationInfoRequest.ticketTypeInfos()
        );
        return ApiResponseUtil.success(SuccessCode.OK, orderId);
    }

    //예약 조회 api
    @GetMapping("/ready/{orderId}")
    public ResponseEntity<BaseResponse<?>> getReadyToPayment(
            @PathVariable("orderId") final String orderId,
            @UserId final Long userId
    ) {
        final ReservationInfoResponse reservationInfoResponse = reservationService.getReservationInfo(
                userId,
                reservationInfoRequest.eventId(),
                reservationInfoRequest.couponCode(),
                reservationInfoRequest.totalAmount(),
                reservationInfoRequest.orderId(),
                reservationInfoRequest.ticketTypeInfos()
        );
        return ApiResponseUtil.success(SuccessCode.OK, reservationInfoResponse);
    }
}
