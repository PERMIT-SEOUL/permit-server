package com.permitseoul.permitserver.domain.admin.base.api.dto.res;

import com.permitseoul.permitserver.domain.user.core.domain.UserRole;

public record UserAuthorityGetResponse(
        long userId,
        String userName,
        UserRole currentUserRole
) {
    public static UserAuthorityGetResponse of(final long userId,
                                              final String userName,
                                              final UserRole userRole) {
        return new UserAuthorityGetResponse(userId,userName, userRole);
    }
}
