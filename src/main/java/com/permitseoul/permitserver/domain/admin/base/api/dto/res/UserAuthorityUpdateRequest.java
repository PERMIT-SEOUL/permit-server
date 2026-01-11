package com.permitseoul.permitserver.domain.admin.base.api.dto.res;

import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import jakarta.validation.constraints.NotNull;

public record UserAuthorityUpdateRequest(
        @NotNull(message = "role이 null입니다.")
        UserRole role
) {
}
