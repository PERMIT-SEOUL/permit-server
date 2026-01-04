package com.permitseoul.permitserver.domain.ticket.api.dto.req;

import jakarta.validation.constraints.NotBlank;

public record TicketConfirmByCameraRequest(
        @NotBlank(message = "ticketCode는 필수입니다.")
        String ticketCode
) {
}
