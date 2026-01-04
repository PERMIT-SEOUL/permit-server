package com.permitseoul.permitserver.domain.guest.api.controller;

import com.permitseoul.permitserver.domain.guest.api.dto.req.GuestTicketConfirmByCameraRequest;
import com.permitseoul.permitserver.domain.guest.api.service.GuestService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class GuestStaffController {
    private final GuestService guestService;

    //도어용 게스트 티켓 스텝 카메라 확인 API
    @PostMapping("/guests/tickets/door/confirm")
    public ResponseEntity<BaseResponse<?>> confirmGuestTicketByStaffCamera(
            @RequestBody @Valid final GuestTicketConfirmByCameraRequest guestTicketConfirmByCameraRequest
    ) {
        guestService.confirmGuestTicketByStaffCamera(guestTicketConfirmByCameraRequest.ticketCode());
        return ApiResponseUtil.success(SuccessCode.OK);
    }

}
