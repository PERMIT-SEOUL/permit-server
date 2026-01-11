package com.permitseoul.permitserver.domain.admin.base.api.dto.res;

import com.permitseoul.permitserver.domain.user.core.domain.UserRole;

public record UserAuthorityGetResponse(
        String userName,
        UserRole currentUserRole
) {
    public static UserAuthorityGetResponse of(final String userName,
                                              final UserRole userRole) {
        return new UserAuthorityGetResponse(userName, userRole);
    }
}
