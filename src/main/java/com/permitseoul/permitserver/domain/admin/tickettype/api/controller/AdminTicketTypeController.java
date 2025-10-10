package com.permitseoul.permitserver.domain.admin.tickettype.api.controller;

import com.permitseoul.permitserver.domain.admin.tickettype.api.service.AdminTicketTypeService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/tickets/types")
@RequiredArgsConstructor
public class AdminTicketTypeController {
    private final AdminTicketTypeService adminTicketTypeService;

    //admin 행사 티켓 타입 삭제 API
    @DeleteMapping("/{ticketTypeId}")
    public ResponseEntity<BaseResponse<?>> deleteTicketType(
            @PathVariable("ticketTypeId") final long ticketTypeId
    ) {
        adminTicketTypeService.deleteTicketType(ticketTypeId);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
