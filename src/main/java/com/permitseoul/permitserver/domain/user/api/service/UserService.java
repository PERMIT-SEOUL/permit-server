package com.permitseoul.permitserver.domain.user.api.service;

import com.permitseoul.permitserver.domain.user.api.dto.UserInfoResponse;
import com.permitseoul.permitserver.domain.user.api.exception.ConflictUserException;
import com.permitseoul.permitserver.domain.user.api.exception.NotfoundUserException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.component.UserUpdater;
import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import com.permitseoul.permitserver.domain.user.core.exception.UserDuplicateException;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRetriever userRetriever;
    private final UserUpdater userUpdater;

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
        final User user;
        try {
            user = userRetriever.findUserById(userId);
        } catch (UserNotFoundException e) {
            throw new NotfoundUserException(ErrorCode.NOT_FOUND_USER);
        }
        return UserInfoResponse.of(user.getName(), user.getAge(), user.getGender(), user.getEmail());
    }

    @Transactional
    public void updateUserInfo(final long userId, final String name, final Gender gender, final String email) {
        final UserEntity userEntity;
        try {
            userEntity = userRetriever.findUserEntityById(userId);
            if (email != null) {
                userRetriever.validEmailDuplicated(email);
            }
            userUpdater.updateUserInfo(userEntity, name, gender, email);
        } catch (UserNotFoundException e) {
            throw new NotfoundUserException(ErrorCode.NOT_FOUND_USER);
        } catch (UserDuplicateException e) {
            throw new ConflictUserException(ErrorCode.CONFLICT_USER_EMAIL);
        }
    }
}
