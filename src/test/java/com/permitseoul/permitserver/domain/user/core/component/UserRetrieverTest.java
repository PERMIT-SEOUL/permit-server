package com.permitseoul.permitserver.domain.user.core.component;

import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import com.permitseoul.permitserver.domain.user.core.exception.UserDuplicateException;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.domain.user.core.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("UserRetriever 테스트")
@ExtendWith(MockitoExtension.class)
class UserRetrieverTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRetriever userRetriever;

    private UserEntity createTestEntity() {
        final UserEntity entity = UserEntity.create("홍길동", Gender.MALE, 25, "test@example.com", "kakao_123",
                SocialType.KAKAO, UserRole.USER);
        ReflectionTestUtils.setField(entity, "userId", 100L);
        return entity;
    }

    @Nested
    @DisplayName("getUserBySocialInfo 메서드")
    class GetUserBySocialInfo {

        @Test
        @DisplayName("존재하면 User를 반환한다")
        void returnsUserWhenFound() {
            // given
            given(userRepository.findUserBySocialTypeAndSocialId(SocialType.KAKAO, "kakao_123"))
                    .willReturn(Optional.of(createTestEntity()));

            // when
            final User result = userRetriever.getUserBySocialInfo(SocialType.KAKAO, "kakao_123");

            // then
            assertThat(result.getUserId()).isEqualTo(100L);
            assertThat(result.getSocialType()).isEqualTo(SocialType.KAKAO);
        }

        @Test
        @DisplayName("존재하지 않으면 UserNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(userRepository.findUserBySocialTypeAndSocialId(SocialType.KAKAO, "invalid"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userRetriever.getUserBySocialInfo(SocialType.KAKAO, "invalid"))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validDuplicatedUserBySocial 메서드")
    class ValidDuplicatedUserBySocial {

        @Test
        @DisplayName("중복이 아니면 예외가 발생하지 않는다")
        void doesNotThrowWhenNotDuplicated() {
            // given
            given(userRepository.existsBySocialTypeAndSocialId(SocialType.KAKAO, "new_user"))
                    .willReturn(false);

            // when & then
            assertThatCode(() -> userRetriever.validDuplicatedUserBySocial(SocialType.KAKAO, "new_user"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("중복이면 UserDuplicateException을 던진다")
        void throwsExceptionWhenDuplicated() {
            // given
            given(userRepository.existsBySocialTypeAndSocialId(SocialType.KAKAO, "kakao_123"))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> userRetriever.validDuplicatedUserBySocial(SocialType.KAKAO, "kakao_123"))
                    .isInstanceOf(UserDuplicateException.class);
        }
    }

    @Nested
    @DisplayName("validExistUserById 메서드")
    class ValidExistUserById {

        @Test
        @DisplayName("존재하면 예외가 발생하지 않는다")
        void doesNotThrowWhenExists() {
            // given
            given(userRepository.existsById(100L)).willReturn(true);

            // when & then
            assertThatCode(() -> userRetriever.validExistUserById(100L))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("존재하지 않으면 UserNotFoundException을 던진다")
        void throwsExceptionWhenNotExists() {
            // given
            given(userRepository.existsById(999L)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> userRetriever.validExistUserById(999L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findUserById 메서드")
    class FindUserById {

        @Test
        @DisplayName("존재하면 User를 반환한다")
        void returnsUserWhenFound() {
            // given
            given(userRepository.findById(100L)).willReturn(Optional.of(createTestEntity()));

            // when
            final User result = userRetriever.findUserById(100L);

            // then
            assertThat(result.getUserId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("존재하지 않으면 UserNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userRetriever.findUserById(999L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findUserEntityById 메서드")
    class FindUserEntityById {

        @Test
        @DisplayName("존재하면 UserEntity를 반환한다")
        void returnsEntityWhenFound() {
            // given
            given(userRepository.findById(100L)).willReturn(Optional.of(createTestEntity()));

            // when
            final UserEntity result = userRetriever.findUserEntityById(100L);

            // then
            assertThat(result.getUserId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("존재하지 않으면 UserNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userRetriever.findUserEntityById(999L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validEmailDuplicated 메서드")
    class ValidEmailDuplicated {

        @Test
        @DisplayName("중복이 아니면 예외가 발생하지 않는다")
        void doesNotThrowWhenNotDuplicated() {
            // given
            given(userRepository.existsByEmail("new@example.com")).willReturn(false);

            // when & then
            assertThatCode(() -> userRetriever.validEmailDuplicated("new@example.com"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("중복이면 UserDuplicateException을 던진다")
        void throwsExceptionWhenDuplicated() {
            // given
            given(userRepository.existsByEmail("test@example.com")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userRetriever.validEmailDuplicated("test@example.com"))
                    .isInstanceOf(UserDuplicateException.class);
        }
    }

    @Nested
    @DisplayName("findUserByEmail 메서드")
    class FindUserByEmail {

        @Test
        @DisplayName("존재하면 User를 반환한다")
        void returnsUserWhenFound() {
            // given
            given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(createTestEntity()));

            // when
            final User result = userRetriever.findUserByEmail("test@example.com");

            // then
            assertThat(result.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("존재하지 않으면 UserNotFoundException을 던진다")
        void throwsExceptionWhenNotFound() {
            // given
            given(userRepository.findByEmail("invalid@example.com")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userRetriever.findUserByEmail("invalid@example.com"))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
