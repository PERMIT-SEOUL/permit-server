package com.permitseoul.permitserver.domain.admin.base.api.dto.req;

import jakarta.validation.constraints.NotBlank;

public record AdminValidateRequest(
        @NotBlank(message = "어드민코드가 빈 값입니다.")
        String adminCode
) {
}
