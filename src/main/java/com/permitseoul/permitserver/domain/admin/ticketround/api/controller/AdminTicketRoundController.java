package com.permitseoul.permitserver.domain.admin.ticketround.api.controller;

import com.permitseoul.permitserver.domain.admin.ticketround.api.service.AdminTicketRoundService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tickets/rounds")
@RequiredArgsConstructor
public class AdminTicketRoundController {
    private final AdminTicketRoundService adminTicketRoundService;

    //admin 행사 티켓 라운드 삭제 API
    @DeleteMapping("/{ticketRoundId}")
    public ResponseEntity<BaseResponse<?>> deleteTicketRound(
            @PathVariable("ticketRoundId") final long ticketRoundId
    ) {
        adminTicketRoundService.deleteTicketRound(ticketRoundId);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
