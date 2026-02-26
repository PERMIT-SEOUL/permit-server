package com.permitseoul.permitserver.domain.user.api.service;

import com.permitseoul.permitserver.domain.user.api.dto.UserInfoResponse;
import com.permitseoul.permitserver.domain.user.api.exception.ConflictUserException;
import com.permitseoul.permitserver.domain.user.api.exception.NotfoundUserException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.component.UserUpdater;
import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import com.permitseoul.permitserver.domain.user.core.exception.UserDuplicateException;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private UserRetriever userRetriever;
    @Mock
    private UserUpdater userUpdater;
    @InjectMocks
    private UserService userService;

    private static final long USER_ID = 1L;

    private User createUser() {
        return new User(USER_ID, "홍길동", Gender.MALE, 25, "test@email.com", "social123", SocialType.KAKAO,
                UserRole.USER);
    }

    @Nested
    @DisplayName("checkEmailDuplicated")
    class CheckEmailDuplicatedTest {

        @Test
        @DisplayName("정상: 중복되지 않은 이메일")
        void success() {
            doNothing().when(userRetriever).validEmailDuplicated("new@email.com");

            userService.checkEmailDuplicated("new@email.com");

            verify(userRetriever).validEmailDuplicated("new@email.com");
        }

        @Test
        @DisplayName("예외: 이메일 중복 → ConflictUserException")
        void throwsWhenDuplicated() {
            doThrow(new UserDuplicateException()).when(userRetriever).validEmailDuplicated("dup@email.com");

            assertThatThrownBy(() -> userService.checkEmailDuplicated("dup@email.com"))
                    .isInstanceOf(ConflictUserException.class);
        }
    }

    @Nested
    @DisplayName("getUserInfo")
    class GetUserInfoTest {

        @Test
        @DisplayName("정상: 사용자 정보 조회")
        void success() {
            when(userRetriever.findUserById(USER_ID)).thenReturn(createUser());

            final UserInfoResponse result = userService.getUserInfo(USER_ID);

            assertThat(result.name()).isEqualTo("홍길동");
            assertThat(result.age()).isEqualTo(25);
            assertThat(result.gender()).isEqualTo(Gender.MALE);
            assertThat(result.email()).isEqualTo("test@email.com");
            assertThat(result.role()).isEqualTo(UserRole.USER);
        }

        @Test
        @DisplayName("예외: 사용자 미존재 → NotfoundUserException")
        void throwsWhenNotFound() {
            when(userRetriever.findUserById(USER_ID)).thenThrow(new UserNotFoundException());

            assertThatThrownBy(() -> userService.getUserInfo(USER_ID))
                    .isInstanceOf(NotfoundUserException.class);
        }
    }

    @Nested
    @DisplayName("updateUserInfo")
    class UpdateUserInfoTest {

        @Test
        @DisplayName("정상: 이메일 없이 사용자 정보 수정")
        void successWithoutEmail() {
            final UserEntity userEntity = UserEntity.create("홍길동", Gender.MALE, 25, "old@email.com", "social123",
                    SocialType.KAKAO, UserRole.USER);
            when(userRetriever.findUserEntityById(USER_ID)).thenReturn(userEntity);

            userService.updateUserInfo(USER_ID, "김길동", Gender.FEMALE, null);

            verify(userUpdater).updateUserInfo(userEntity, "김길동", Gender.FEMALE, null);
            verify(userRetriever, never()).validEmailDuplicated(any());
        }

        @Test
        @DisplayName("정상: 이메일 포함 사용자 정보 수정")
        void successWithEmail() {
            final UserEntity userEntity = UserEntity.create("홍길동", Gender.MALE, 25, "old@email.com", "social123",
                    SocialType.KAKAO, UserRole.USER);
            when(userRetriever.findUserEntityById(USER_ID)).thenReturn(userEntity);
            doNothing().when(userRetriever).validEmailDuplicated("new@email.com");

            userService.updateUserInfo(USER_ID, "김길동", Gender.FEMALE, "new@email.com");

            verify(userRetriever).validEmailDuplicated("new@email.com");
            verify(userUpdater).updateUserInfo(userEntity, "김길동", Gender.FEMALE, "new@email.com");
        }

        @Test
        @DisplayName("예외: 사용자 미존재 → NotfoundUserException")
        void throwsWhenUserNotFound() {
            when(userRetriever.findUserEntityById(USER_ID)).thenThrow(new UserNotFoundException());

            assertThatThrownBy(() -> userService.updateUserInfo(USER_ID, "김길동", Gender.MALE, null))
                    .isInstanceOf(NotfoundUserException.class);
        }

        @Test
        @DisplayName("예외: 이메일 중복 → ConflictUserException")
        void throwsWhenEmailDuplicated() {
            final UserEntity userEntity = UserEntity.create("홍길동", Gender.MALE, 25, "old@email.com", "social123",
                    SocialType.KAKAO, UserRole.USER);
            when(userRetriever.findUserEntityById(USER_ID)).thenReturn(userEntity);
            doThrow(new UserDuplicateException()).when(userRetriever).validEmailDuplicated("dup@email.com");

            assertThatThrownBy(() -> userService.updateUserInfo(USER_ID, "김길동", Gender.MALE, "dup@email.com"))
                    .isInstanceOf(ConflictUserException.class);
        }
    }
}
