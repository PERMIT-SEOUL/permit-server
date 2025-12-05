package com.permitseoul.permitserver.domain.admin.event.api.dto.req;

import jakarta.validation.constraints.NotBlank;

public record AdminEventImageRequest(
        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl
) {
}
