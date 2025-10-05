package com.permitseoul.permitserver.domain.admin.guestticket.api.controller;

import com.permitseoul.permitserver.domain.admin.guestticket.api.dto.request.GuestTicketIssueRequest;
import com.permitseoul.permitserver.domain.admin.guestticket.api.service.AdminGuestTicketService;
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
@RequestMapping("/api/admin/guests/tickets")
@RequiredArgsConstructor
public class AdminGuestTicketController {
    private final AdminGuestTicketService adminGuestTicketService;


    //게스트 티켓 생성 API
    @PostMapping()
    public ResponseEntity<BaseResponse<?>> issueGuestTickets(
            @RequestBody @Valid GuestTicketIssueRequest guestTicketIssueRequest
    ) {
        adminGuestTicketService.issueGuestTickets(guestTicketIssueRequest.eventId(), guestTicketIssueRequest.guestTicketList());
        return ApiResponseUtil.success(SuccessCode.CREATED);
    }

}
