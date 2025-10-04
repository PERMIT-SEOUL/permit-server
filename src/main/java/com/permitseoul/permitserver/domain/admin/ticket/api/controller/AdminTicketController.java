package com.permitseoul.permitserver.domain.admin.ticket.api.controller;

import com.permitseoul.permitserver.domain.admin.ticket.api.dto.req.TicketRoundWithTypeCreateRequest;
import com.permitseoul.permitserver.domain.admin.ticket.api.service.AdminTicketService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
public class AdminTicketController {
    private final AdminTicketService adminTicketService;

    //admin 행사 티켓 라운드+타입 상세 조회 API
    @GetMapping("/details/{ticketRoundId}")
    public ResponseEntity<BaseResponse<?>> getTicketRoundAndTicketTypeDetails(
        @PathVariable("ticketRoundId") final long ticketRoundId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, adminTicketService.getTicketRoundAndTypeDetails(ticketRoundId));
    }

    //admin 행사 티켓 라운드+타입 전체 조회 API
    @GetMapping("/{eventId}")
    public ResponseEntity<BaseResponse<?>> getTicketRoundsWithTicketType(
            @PathVariable("eventId") final long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, adminTicketService.getTicketRoundWithTicketType(eventId));
    }

    //admin 행사 티켓 라운드+타입 등록 API
    @PostMapping("/{eventId}")
    public ResponseEntity<BaseResponse<?>> createTicketRoundWithType(
            @PathVariable("eventId") final long eventId,
            @RequestBody final TicketRoundWithTypeCreateRequest ticketRoundWithTypeCreateRequest
    ) {
        adminTicketService.createTicketRoundWithType(
                eventId,
                ticketRoundWithTypeCreateRequest.ticketRoundName(),
                ticketRoundWithTypeCreateRequest.ticketRoundSalesStartDate(),
                ticketRoundWithTypeCreateRequest.ticketRoundSalesEndDate(),
                ticketRoundWithTypeCreateRequest.ticketTypes()
        );
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
