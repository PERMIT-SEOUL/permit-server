package com.permitseoul.permitserver.domain.reservation.api.controller;

import com.permitseoul.permitserver.domain.auth.core.jwt.CookieCreatorUtil;
import com.permitseoul.permitserver.domain.auth.core.jwt.CookieExtractor;
import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoRequest;
import com.permitseoul.permitserver.domain.reservation.api.dto.*;
import com.permitseoul.permitserver.domain.reservation.api.service.ReservationService;
import com.permitseoul.permitserver.global.domain.CookieType;
import com.permitseoul.permitserver.global.resolver.user.UserId;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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
            @UserId final Long userId,
            @RequestBody @Valid final ReservationInfoRequest reservationInfoRequest,
            final HttpServletResponse response
    ) {
        final String sessionKey = reservationService.saveReservation(
                userId,
                reservationInfoRequest.eventId(),
                reservationInfoRequest.couponCode(),
                reservationInfoRequest.totalAmount(),
                reservationInfoRequest.orderId(),
                reservationInfoRequest.ticketTypeInfos()
        );
        final ResponseCookie reservationSessionCookie = CookieCreatorUtil.createReservationSessionCookie(sessionKey);
        response.setHeader("Set-Cookie", reservationSessionCookie.toString());
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    //예약 조회 api
    @GetMapping("/ready/")
    public ResponseEntity<BaseResponse<?>> getReadyToPayment(
            @UserId final Long userId,
            final HttpServletRequest request
            ) {
        final String reservationSessionKey = CookieExtractor.extractCookie(request, CookieType.RESERVATION_SESSION).getValue();
        final ReservationInfoResponse reservationInfoResponse = reservationService.getReservationInfo(userId, reservationSessionKey);
        return ApiResponseUtil.success(SuccessCode.OK, reservationInfoResponse);
    }
}
