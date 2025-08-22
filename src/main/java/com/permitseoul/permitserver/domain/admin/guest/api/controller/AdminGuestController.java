package com.permitseoul.permitserver.domain.admin.guest.api.controller;

import com.permitseoul.permitserver.domain.admin.guest.api.dto.GuestAddRequest;
import com.permitseoul.permitserver.domain.admin.guest.api.service.AdminGuestService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/guests")
@RequiredArgsConstructor
public class AdminGuestController {
    private final AdminGuestService adminGuestService;

    //게스트 리스트 조회 API
    @GetMapping()
    public ResponseEntity<BaseResponse<?>> getGuestList() {
        return ApiResponseUtil.success(SuccessCode.OK, adminGuestService.getGuestList());
    }

    //게스트 추가 API
    @PostMapping()
    public ResponseEntity<BaseResponse<?>> addGuest(
            @RequestBody @Valid GuestAddRequest guestAddRequest
    ) {
        adminGuestService.addGuest(
                guestAddRequest.guestName(),
                guestAddRequest.guestType(),
                guestAddRequest.affiliation(),
                guestAddRequest.phoneNumber(),
                guestAddRequest.email()
        );
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
