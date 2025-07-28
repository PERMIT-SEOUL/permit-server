package com.permitseoul.permitserver.domain.user.api.service;

import com.permitseoul.permitserver.domain.user.api.dto.UserInfoResponse;
import com.permitseoul.permitserver.domain.user.api.exception.ConflictUserException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.exception.UserDuplicateException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRetriever userRetriever;

    @Transactional(readOnly = true)
    public void checkEmailDuplicated(final String userEmail) {
       try {
           userRetriever.validEmailDuplicated(userEmail);
       } catch (UserDuplicateException e) {
           throw new ConflictUserException(ErrorCode.CONFLICT_USER_EMAIL);
       }
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(final long userId) {
        final User user = userRetriever.findUserById(userId);
        return UserInfoResponse.of(user.getName(), user.getAge(), user.getGender(), user.getEmail());
    }
}
