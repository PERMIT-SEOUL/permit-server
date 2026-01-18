package com.permitseoul.permitserver.domain.guest.api.dto.req;

import jakarta.validation.constraints.NotBlank;

public record GuestTicketConfirmRequest(
        @NotBlank(message = "ticketCode가 비어있습니다.")
        String ticketCode,

        @NotBlank(message = "checkCode가 비어있습니다.")
        String checkCode
) {
}
