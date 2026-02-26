package com.permitseoul.permitserver.domain.user.core.domain;

import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import com.permitseoul.permitserver.domain.user.core.exception.UserIllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User & UserEntity 테스트")
class UserEntityTest {

    private static final String NAME = "홍길동";
    private static final Gender GENDER = Gender.MALE;
    private static final int AGE = 25;
    private static final String EMAIL = "test@example.com";
    private static final String SOCIAL_ID = "kakao_12345";
    private static final SocialType SOCIAL_TYPE = SocialType.KAKAO;
    private static final UserRole USER_ROLE = UserRole.USER;

    private UserEntity createTestEntity() {
        return UserEntity.create(NAME, GENDER, AGE, EMAIL, SOCIAL_ID, SOCIAL_TYPE, USER_ROLE);
    }

    @Nested
    @DisplayName("UserEntity.create 메서드")
    class Create {

        @Test
        @DisplayName("정상적인 값으로 UserEntity를 생성한다")
        void createsUserEntitySuccessfully() {
            // when
            final UserEntity entity = createTestEntity();

            // then
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getGender()).isEqualTo(GENDER);
            assertThat(entity.getAge()).isEqualTo(AGE);
            assertThat(entity.getEmail()).isEqualTo(EMAIL);
            assertThat(entity.getSocialId()).isEqualTo(SOCIAL_ID);
            assertThat(entity.getSocialType()).isEqualTo(SOCIAL_TYPE);
            assertThat(entity.getUserRole()).isEqualTo(USER_ROLE);
        }

        @Test
        @DisplayName("생성 직후 userId는 null이다 (@GeneratedValue)")
        void userIdIsNullAfterCreate() {
            // when
            final UserEntity entity = createTestEntity();

            // then
            assertThat(entity.getUserId()).isNull();
        }
    }

    @Nested
    @DisplayName("updateUserInfo 메서드")
    class UpdateUserInfo {

        @Test
        @DisplayName("name, gender, email을 정상 업데이트한다")
        void updatesUserInfoSuccessfully() {
            // given
            final UserEntity entity = createTestEntity();
            final String newName = "김철수";
            final Gender newGender = Gender.MALE;
            final String newEmail = "new@example.com";

            // when
            entity.updateUserInfo(newName, newGender, newEmail);

            // then
            assertThat(entity.getName()).isEqualTo(newName);
            assertThat(entity.getGender()).isEqualTo(newGender);
            assertThat(entity.getEmail()).isEqualTo(newEmail);
        }

        @Test
        @DisplayName("email이 null이면 기존 email을 유지한다")
        void keepsOriginalEmailWhenNewEmailIsNull() {
            // given
            final UserEntity entity = createTestEntity();
            final String originalEmail = entity.getEmail();

            // when
            entity.updateUserInfo("새이름", Gender.FEMALE, null);

            // then
            assertThat(entity.getEmail()).isEqualTo(originalEmail);
            assertThat(entity.getName()).isEqualTo("새이름");
            assertThat(entity.getGender()).isEqualTo(Gender.FEMALE);
        }
    }

    @Nested
    @DisplayName("updateUserRole 메서드")
    class UpdateUserRole {

        @Test
        @DisplayName("USER → ADMIN으로 역할을 변경한다")
        void updatesRoleFromUserToAdmin() {
            // given
            final UserEntity entity = createTestEntity();

            // when
            entity.updateUserRole(UserRole.ADMIN);

            // then
            assertThat(entity.getUserRole()).isEqualTo(UserRole.ADMIN);
        }

        @Test
        @DisplayName("USER → STAFF로 역할을 변경한다")
        void updatesRoleFromUserToStaff() {
            // given
            final UserEntity entity = createTestEntity();

            // when
            entity.updateUserRole(UserRole.STAFF);

            // then
            assertThat(entity.getUserRole()).isEqualTo(UserRole.STAFF);
        }

        @Test
        @DisplayName("null을 전달하면 UserIllegalArgumentException을 던진다")
        void throwsExceptionWhenRoleIsNull() {
            // given
            final UserEntity entity = createTestEntity();

            // when & then
            assertThatThrownBy(() -> entity.updateUserRole(null))
                    .isInstanceOf(UserIllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("User.fromEntity 메서드")
    class FromEntity {

        @Test
        @DisplayName("Entity의 모든 필드가 Domain 객체로 정확히 매핑된다")
        void mapsAllFieldsCorrectly() {
            // given
            final UserEntity entity = createTestEntity();
            ReflectionTestUtils.setField(entity, "userId", 100L);

            // when
            final User user = User.fromEntity(entity);

            // then
            assertThat(user.getUserId()).isEqualTo(100L);
            assertThat(user.getName()).isEqualTo(NAME);
            assertThat(user.getGender()).isEqualTo(GENDER);
            assertThat(user.getAge()).isEqualTo(AGE);
            assertThat(user.getEmail()).isEqualTo(EMAIL);
            assertThat(user.getSocialId()).isEqualTo(SOCIAL_ID);
            assertThat(user.getSocialType()).isEqualTo(SOCIAL_TYPE);
            assertThat(user.getUserRole()).isEqualTo(USER_ROLE);
        }
    }
}
