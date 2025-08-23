package com.permitseoul.permitserver.domain.admin.api.service;

import com.permitseoul.permitserver.domain.admin.api.AdminProperties;
import com.permitseoul.permitserver.domain.admin.api.exception.AdminAuthorizationException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final AdminProperties adminProperties;

    public void validateAdminCode(final String adminCode) {
        if(!(adminProperties.accessCode().equals(adminCode))){
            throw new AdminAuthorizationException(ErrorCode.UNAUTHORIZED_ADMIN_ACCESS_CODE);
        }
    }
}
