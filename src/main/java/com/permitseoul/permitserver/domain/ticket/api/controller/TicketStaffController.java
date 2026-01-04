package com.permitseoul.permitserver.domain.ticket.api.controller;

import com.permitseoul.permitserver.domain.ticket.api.dto.req.TicketConfirmByCameraRequest;
import com.permitseoul.permitserver.domain.ticket.api.service.TicketService;
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
@RequiredArgsConstructor
@RequestMapping("/api/staff")
public class TicketStaffController {
    private final TicketService ticketService;

    //도어용 유저 티켓 스텝 카메라 확인 api
    @PostMapping("/tickets/door/confirm")
    public ResponseEntity<BaseResponse<?>> confirmUserTicketByStaffCamera(
            @RequestBody @Valid final TicketConfirmByCameraRequest ticketConfirmByCameraRequest
    ) {
        ticketService.confirmTicketByStaffCamera(ticketConfirmByCameraRequest.ticketCode());
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
