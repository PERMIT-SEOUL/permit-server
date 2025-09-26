package com.permitseoul.permitserver.domain.ticket.api.dto.req;

import jakarta.validation.constraints.NotBlank;

public record TicketConfirmRequest(
        @NotBlank(message = "ticketCode가 비어있습니다.")
        String ticketCode,

        @NotBlank(message = "checkCode가 비어있습니다.")
        String checkCode
) {
}
