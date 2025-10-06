package com.permitseoul.permitserver.domain.reservation.api.controller;

import com.permitseoul.permitserver.domain.auth.core.jwt.CookieCreatorUtil;
import com.permitseoul.permitserver.domain.auth.core.jwt.CookieExtractor;
import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoRequest;
import com.permitseoul.permitserver.domain.reservation.api.dto.*;
import com.permitseoul.permitserver.domain.reservation.api.exception.NotfoundReservationException;
import com.permitseoul.permitserver.domain.reservation.api.exception.ReservationSessionCookieException;
import com.permitseoul.permitserver.domain.reservation.api.service.ReservationService;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.domain.CookieType;
import com.permitseoul.permitserver.global.aop.resolver.user.UserIdHeader;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import com.permitseoul.permitserver.global.util.SecureUrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final SecureUrlUtil secureUrlUtil;

    //예약 생성 api
    @PostMapping("/ready")
    public ResponseEntity<BaseResponse<?>> saveReservation(
            @UserIdHeader final Long userId,
            @RequestBody @Valid final ReservationInfoRequest reservationInfoRequest,
            final HttpServletResponse response
    ) {
        final String sessionKey = reservationService.saveReservation(
                userId,
                secureUrlUtil.decode(reservationInfoRequest.eventId()),
                reservationInfoRequest.couponCode(),
                reservationInfoRequest.totalAmount(),
                reservationInfoRequest.orderId(),
                reservationInfoRequest.ticketTypeInfos(),
                LocalDateTime.now()
        );
        final ResponseCookie reservationSessionCookie = CookieCreatorUtil.createReservationSessionCookie(sessionKey);
        response.setHeader(Constants.SET_COOKIE, reservationSessionCookie.toString());
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    //예약 조회 api
    @GetMapping("/ready")
    public ResponseEntity<BaseResponse<?>> getReadyToPayment(
            @UserIdHeader final Long userId,
            final HttpServletRequest request
    ) {
        String reservationSessionKey;
        try {
            reservationSessionKey = CookieExtractor.extractCookie(request, CookieType.RESERVATION_SESSION).getValue();
        } catch (ReservationSessionCookieException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_RESERVATION_SESSION_COOKIE);
        }
        final ReservationInfoResponse reservationInfoResponse = reservationService.getReservationInfo(userId, reservationSessionKey);
        return ApiResponseUtil.success(SuccessCode.OK, reservationInfoResponse);
    }
}
