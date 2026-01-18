package com.permitseoul.permitserver.domain.admin.base.api.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserAuthorityGetRequest(
        @NotBlank
        @Email(message = "email 형식이 틀렸습니다.")
        String email
) {
}
