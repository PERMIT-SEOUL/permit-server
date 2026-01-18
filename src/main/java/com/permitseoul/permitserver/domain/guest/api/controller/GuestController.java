package com.permitseoul.permitserver.domain.guest.api.controller;


import com.permitseoul.permitserver.domain.guest.api.dto.req.GuestTicketConfirmRequest;
import com.permitseoul.permitserver.domain.guest.api.service.GuestService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {
    private final GuestService guestService;

    //도어용 게스트 티켓 유효성 검증 api
    @GetMapping("/tickets/door/validation/{ticketCode}")
    public ResponseEntity<BaseResponse<?>> validateGuestTicket(
            @PathVariable final String ticketCode
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, guestService.validateGuestTicket(ticketCode));
    }

    //도어용 게스트 티켓 스텝 코드 확인 api
    @PostMapping("/tickets/door/staff/confirm")
    public ResponseEntity<BaseResponse<?>> confirmGuestTicketByStaffAtDoor(
            @RequestBody @Valid GuestTicketConfirmRequest guestTicketConfirmRequest
    ) {
        guestService.confirmGuestTicketByStaffCheckCode(guestTicketConfirmRequest.ticketCode(), guestTicketConfirmRequest.checkCode());
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
