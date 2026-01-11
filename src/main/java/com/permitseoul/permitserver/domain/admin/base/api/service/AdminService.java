package com.permitseoul.permitserver.domain.admin.base.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.AdminProperties;
import com.permitseoul.permitserver.domain.admin.base.api.dto.req.UserAuthorityGetRequest;
import com.permitseoul.permitserver.domain.admin.base.api.dto.res.UserAuthorityGetResponse;
import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminAuthorizationException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.component.UserUpdater;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import com.permitseoul.permitserver.domain.user.core.exception.UserIllegalArgumentException;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final AdminProperties adminProperties;
    private final UserRetriever userRetriever;
    private final UserUpdater userUpdater;

    public void validateAdminCode(final String adminCode) {
        if(!(adminProperties.accessCode().equals(adminCode))){
            throw new AdminAuthorizationException(ErrorCode.UNAUTHORIZED_ADMIN_ACCESS_CODE);
        }
    }

    @Transactional(readOnly = true)
    public UserAuthorityGetResponse getUserAuthority(final String email) {
        final User user;
        try {
            user = userRetriever.findUserByEmail(email);
        } catch (UserNotFoundException e) {
            throw new AdminAuthorizationException(ErrorCode.NOT_FOUND_USER);
        }
        return UserAuthorityGetResponse.of(user.getUserId(), user.getName(), user.getUserRole());
    }

    @Transactional
    public void updateUserAuthority(final long userId, final UserRole userRole) {
        final UserEntity userEntity;
        try {
            userEntity = userRetriever.findUserEntityById(userId);
            userUpdater.updateUserRole(userEntity, userRole);
        } catch (UserNotFoundException e) {
            throw new AdminAuthorizationException(ErrorCode.NOT_FOUND_USER);
        }
    }
}
