package com.permitseoul.permitserver.domain.guest.api.controller;


import com.permitseoul.permitserver.domain.guest.api.service.GuestService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {
    private final GuestService guestService;

    //게스트 티켓 유효성 검증 api
    @GetMapping("/tickets/door/validation/{ticketCode}")
    public ResponseEntity<BaseResponse<?>> validateGuestTicket(
            @PathVariable final String ticketCode
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, guestService.validateGuestTicket(ticketCode));
    }
}
