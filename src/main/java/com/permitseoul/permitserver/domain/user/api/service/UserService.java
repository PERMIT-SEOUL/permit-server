package com.permitseoul.permitserver.domain.user.api.service;

import com.permitseoul.permitserver.domain.user.api.exception.ConflictUserException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.exception.UserDuplicateException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRetriever userRetriever;

    public void checkEmailDuplicated(final String userEmail) {
       try {
           userRetriever.validEmailDuplicated(userEmail);
       } catch (UserDuplicateException e) {
           throw new ConflictUserException(ErrorCode.CONFLICT_USER_EMAIL);
       }
    }
}
