package com.permitseoul.permitserver.domain.guest.api.dto.req;

import jakarta.validation.constraints.NotBlank;

public record GuestTicketConfirmByCameraRequest(
        @NotBlank(message = "ticketCode는 필수입니다.")
        String ticketCode
) {
}
