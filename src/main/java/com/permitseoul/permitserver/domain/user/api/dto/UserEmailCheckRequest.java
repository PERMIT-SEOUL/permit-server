package com.permitseoul.permitserver.domain.user.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UserEmailCheckRequest(
        @NotBlank(message = "이메일이 비어있으면 안됩니다.")
        String userEmail
) {
}
