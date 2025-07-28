package com.permitseoul.permitserver.domain.ticket.api.controller;

import com.permitseoul.permitserver.domain.ticket.api.service.TicketService;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketUsability;
import com.permitseoul.permitserver.global.resolver.event.EventIdPathVariable;
import com.permitseoul.permitserver.global.resolver.user.UserIdHeader;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    //행사 티켓 정보 조회 api
    @GetMapping("/{eventId}")
    public ResponseEntity<BaseResponse<?>> getEventTicketInfo(
            @EventIdPathVariable final Long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, ticketService.getEventTicketInfo(eventId, LocalDateTime.now()));
    }

    //구매한 티켓 정보 조회
    @GetMapping("/user")
    public ResponseEntity<BaseResponse<?>> getUserBuyTicketInfo(
            @UserIdHeader final Long userId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, ticketService.getUserBuyTicketInfo(userId));
    }
}
