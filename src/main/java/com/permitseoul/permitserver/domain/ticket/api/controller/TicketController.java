package com.permitseoul.permitserver.domain.ticket.api.controller;

import com.permitseoul.permitserver.domain.ticket.api.dto.req.TicketConfirmRequest;
import com.permitseoul.permitserver.domain.ticket.api.service.TicketService;
import com.permitseoul.permitserver.global.aop.resolver.event.EventIdPathVariable;
import com.permitseoul.permitserver.global.aop.resolver.user.UserIdHeader;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    //행사 티켓 정보 조회 api
    @GetMapping("/info/{eventId}")
    public ResponseEntity<BaseResponse<?>> getEventTicketInfo(
            @EventIdPathVariable final Long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, ticketService.getEventTicketInfo(eventId, LocalDateTime.now()));
    }

    //구매한 티켓 정보 조회 api
    @GetMapping("/user")
    public ResponseEntity<BaseResponse<?>> getUserBuyTicketInfo(
            @UserIdHeader final Long userId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, ticketService.getUserBuyTicketInfo(userId));
    }

    //도어용 유저 티켓 스텝 확인 api
    @PostMapping("/door/staff/confirm")
    public ResponseEntity<BaseResponse<?>> confirmUserTicketByStaffAtDoor(
            @RequestBody @Valid TicketConfirmRequest ticketConfirmRequest
    ) {
        ticketService.confirmTicketByStaff(ticketConfirmRequest.ticketCode(), ticketConfirmRequest.checkCode());
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    //도어용 유저 티켓 유효성 검증 api
    @GetMapping("/door/validation/{ticketCode}")
    public ResponseEntity<BaseResponse<?>> validateUserTicketAtDoor(
            @PathVariable("ticketCode") final String ticketCode
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, ticketService.validateUserTicket(ticketCode));
    }
}
