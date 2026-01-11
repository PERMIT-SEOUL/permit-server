package com.permitseoul.permitserver.domain.admin.base.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.AdminProperties;
import com.permitseoul.permitserver.domain.admin.base.api.dto.req.UserAuthorityGetRequest;
import com.permitseoul.permitserver.domain.admin.base.api.dto.res.UserAuthorityGetResponse;
import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminAuthorizationException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final AdminProperties adminProperties;
    private final UserRetriever userRetriever;
    
    public void validateAdminCode(final String adminCode) {
        if(!(adminProperties.accessCode().equals(adminCode))){
            throw new AdminAuthorizationException(ErrorCode.UNAUTHORIZED_ADMIN_ACCESS_CODE);
        }
    }

    public UserAuthorityGetResponse getUserAuthority(final String email) {
        final User user;
        try {
            user = userRetriever.findUserByEmail(email);
        } catch (UserNotFoundException e) {
            throw new AdminAuthorizationException(ErrorCode.NOT_FOUND_USER);
        }
        return UserAuthorityGetResponse.of(user.getName(), user.getUserRole());
    }
}
