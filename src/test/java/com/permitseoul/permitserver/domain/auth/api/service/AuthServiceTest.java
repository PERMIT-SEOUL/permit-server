package com.permitseoul.permitserver.domain.auth.api.service;

import com.permitseoul.permitserver.domain.auth.api.dto.TokenDto;
import com.permitseoul.permitserver.domain.auth.api.exception.AuthRedisException;
import com.permitseoul.permitserver.domain.auth.api.exception.AuthSocialNotFoundApiException;
import com.permitseoul.permitserver.domain.auth.api.exception.AuthUnAuthorizedException;
import com.permitseoul.permitserver.domain.auth.api.exception.AuthUnAuthorizedFeignException;
import com.permitseoul.permitserver.domain.auth.core.domain.Token;
import com.permitseoul.permitserver.domain.auth.core.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.domain.auth.core.exception.*;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProperties;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProvider;
import com.permitseoul.permitserver.domain.auth.core.jwt.RefreshTokenManager;
import com.permitseoul.permitserver.domain.auth.core.strategy.LoginStrategy;
import com.permitseoul.permitserver.domain.auth.core.strategy.LoginStrategyManager;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.component.UserSaver;
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
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Mock
    private LoginStrategyManager loginStrategyManager;
    @Mock
    private UserSaver userSaver;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserRetriever userRetriever;
    @Mock
    private RefreshTokenManager refreshTokenManager;
    @Mock
    private JwtProperties jwtProperties;
    @InjectMocks
    private AuthService authService;

    private static final long USER_ID = 1L;
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String SOCIAL_ACCESS_TOKEN = "social-access-token";
    private static final String SOCIAL_ID = "social-123";
    private static final String AUTH_CODE = "auth-code";
    private static final String REDIRECT_URL = "https://redirect.com";

    private Token createToken() {
        return Token.of(ACCESS_TOKEN, REFRESH_TOKEN);
    }

    private User createUser() {
        return new User(USER_ID, "홍길동", Gender.MALE, 25, "test@email.com", SOCIAL_ID, SocialType.KAKAO, UserRole.USER);
    }

    private LoginStrategy mockLoginStrategy() {
        final LoginStrategy strategy = mock(LoginStrategy.class);
        when(loginStrategyManager.getStrategy(SocialType.KAKAO)).thenReturn(strategy);
        return strategy;
    }

    @Nested
    @DisplayName("signUp")
    class SignUpTest {

        @Test
        @DisplayName("정상: 회원가입 → 토큰 반환")
        void success() {
            final LoginStrategy strategy = mockLoginStrategy();
            when(strategy.getUserSocialId(SOCIAL_ACCESS_TOKEN)).thenReturn(SOCIAL_ID);
            doNothing().when(userRetriever).validDuplicatedUserBySocial(SocialType.KAKAO, SOCIAL_ID);

            final UserEntity savedEntity = UserEntity.create("홍길동", Gender.MALE, 25, "test@email.com", SOCIAL_ID,
                    SocialType.KAKAO, UserRole.USER);
            ReflectionTestUtils.setField(savedEntity, "userId", USER_ID);
            when(userSaver.saveUser(any(UserEntity.class))).thenReturn(savedEntity);
            when(jwtProvider.issueToken(USER_ID, UserRole.USER)).thenReturn(createToken());
            when(jwtProperties.refreshTokenExpirationTime()).thenReturn(604800000L);

            final TokenDto result = authService.signUp("홍길동", 25, Gender.MALE, "test@email.com", SocialType.KAKAO,
                    SOCIAL_ACCESS_TOKEN);

            assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);
            verify(refreshTokenManager).saveRefreshTokenInRedis(eq(USER_ID), eq(REFRESH_TOKEN), anyLong());
        }

        @Test
        @DisplayName("예외: 소셜 API 실패 → AuthUnAuthorizedFeignException")
        void throwsWhenFeignFails() {
            final LoginStrategy strategy = mockLoginStrategy();
            when(strategy.getUserSocialId(SOCIAL_ACCESS_TOKEN))
                    .thenThrow(new AuthPlatformFeignException("KAKAO_ERROR"));

            assertThatThrownBy(() -> authService.signUp("홍길동", 25, Gender.MALE, "test@email.com", SocialType.KAKAO,
                    SOCIAL_ACCESS_TOKEN))
                    .isInstanceOf(AuthUnAuthorizedFeignException.class);
        }

        @Test
        @DisplayName("예외: 중복 사용자 → AuthUnAuthorizedException")
        void throwsWhenDuplicate() {
            final LoginStrategy strategy = mockLoginStrategy();
            when(strategy.getUserSocialId(SOCIAL_ACCESS_TOKEN)).thenReturn(SOCIAL_ID);
            doThrow(new UserDuplicateException()).when(userRetriever).validDuplicatedUserBySocial(SocialType.KAKAO,
                    SOCIAL_ID);

            assertThatThrownBy(() -> authService.signUp("홍길동", 25, Gender.MALE, "test@email.com", SocialType.KAKAO,
                    SOCIAL_ACCESS_TOKEN))
                    .isInstanceOf(AuthUnAuthorizedException.class);
        }

        @Test
        @DisplayName("예외: Redis 저장 실패 → AuthRedisException")
        void throwsWhenRedisFails() {
            final LoginStrategy strategy = mockLoginStrategy();
            when(strategy.getUserSocialId(SOCIAL_ACCESS_TOKEN)).thenReturn(SOCIAL_ID);
            doNothing().when(userRetriever).validDuplicatedUserBySocial(SocialType.KAKAO, SOCIAL_ID);

            final UserEntity savedEntity = UserEntity.create("홍길동", Gender.MALE, 25, "test@email.com", SOCIAL_ID,
                    SocialType.KAKAO, UserRole.USER);
            ReflectionTestUtils.setField(savedEntity, "userId", USER_ID);
            when(userSaver.saveUser(any(UserEntity.class))).thenReturn(savedEntity);
            when(jwtProvider.issueToken(USER_ID, UserRole.USER)).thenReturn(createToken());
            when(jwtProperties.refreshTokenExpirationTime()).thenReturn(604800000L);
            doThrow(new AuthRTException()).when(refreshTokenManager).saveRefreshTokenInRedis(eq(USER_ID),
                    eq(REFRESH_TOKEN), anyLong());

            assertThatThrownBy(() -> authService.signUp("홍길동", 25, Gender.MALE, "test@email.com", SocialType.KAKAO,
                    SOCIAL_ACCESS_TOKEN))
                    .isInstanceOf(AuthRedisException.class);
        }
    }

    @Nested
    @DisplayName("login")
    class LoginTest {

        @Test
        @DisplayName("정상: 로그인 → 토큰 반환")
        void success() {
            final LoginStrategy strategy = mockLoginStrategy();
            final UserSocialInfoDto socialInfo = UserSocialInfoDto.of(SocialType.KAKAO, SOCIAL_ID, SOCIAL_ACCESS_TOKEN);
            when(strategy.getUserSocialInfo(AUTH_CODE, REDIRECT_URL)).thenReturn(socialInfo);
            when(userRetriever.getUserBySocialInfo(SocialType.KAKAO, SOCIAL_ID)).thenReturn(createUser());
            when(jwtProvider.issueToken(USER_ID, UserRole.USER)).thenReturn(createToken());
            when(jwtProperties.refreshTokenExpirationTime()).thenReturn(604800000L);

            final TokenDto result = authService.login(SocialType.KAKAO, AUTH_CODE, REDIRECT_URL);

            assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);
        }

        @Test
        @DisplayName("예외: 소셜 API 실패 → AuthUnAuthorizedFeignException")
        void throwsWhenFeignFails() {
            final LoginStrategy strategy = mockLoginStrategy();
            when(strategy.getUserSocialInfo(AUTH_CODE, REDIRECT_URL))
                    .thenThrow(new AuthPlatformFeignException("KAKAO_ERROR"));

            assertThatThrownBy(() -> authService.login(SocialType.KAKAO, AUTH_CODE, REDIRECT_URL))
                    .isInstanceOf(AuthUnAuthorizedFeignException.class);
        }

        @Test
        @DisplayName("예외: 사용자 미존재 → AuthSocialNotFoundApiException")
        void throwsWhenUserNotFound() {
            final LoginStrategy strategy = mockLoginStrategy();
            final UserSocialInfoDto socialInfo = UserSocialInfoDto.of(SocialType.KAKAO, SOCIAL_ID, SOCIAL_ACCESS_TOKEN);
            when(strategy.getUserSocialInfo(AUTH_CODE, REDIRECT_URL)).thenReturn(socialInfo);
            when(userRetriever.getUserBySocialInfo(SocialType.KAKAO, SOCIAL_ID)).thenThrow(new UserNotFoundException());

            assertThatThrownBy(() -> authService.login(SocialType.KAKAO, AUTH_CODE, REDIRECT_URL))
                    .isInstanceOf(AuthSocialNotFoundApiException.class);
        }

        @Test
        @DisplayName("예외: 빈 소셜 토큰 → AuthUnAuthorizedException")
        void throwsWhenEmptySocialToken() {
            final LoginStrategy strategy = mockLoginStrategy();
            final UserSocialInfoDto socialInfo = UserSocialInfoDto.of(SocialType.KAKAO, SOCIAL_ID, "");
            when(strategy.getUserSocialInfo(AUTH_CODE, REDIRECT_URL)).thenReturn(socialInfo);

            assertThatThrownBy(() -> authService.login(SocialType.KAKAO, AUTH_CODE, REDIRECT_URL))
                    .isInstanceOf(AuthUnAuthorizedException.class);
        }

        @Test
        @DisplayName("예외: Redis 저장 실패 → AuthRedisException")
        void throwsWhenRedisFails() {
            final LoginStrategy strategy = mockLoginStrategy();
            final UserSocialInfoDto socialInfo = UserSocialInfoDto.of(SocialType.KAKAO, SOCIAL_ID, SOCIAL_ACCESS_TOKEN);
            when(strategy.getUserSocialInfo(AUTH_CODE, REDIRECT_URL)).thenReturn(socialInfo);
            when(userRetriever.getUserBySocialInfo(SocialType.KAKAO, SOCIAL_ID)).thenReturn(createUser());
            when(jwtProvider.issueToken(USER_ID, UserRole.USER)).thenReturn(createToken());
            when(jwtProperties.refreshTokenExpirationTime()).thenReturn(604800000L);
            doThrow(new AuthRTException()).when(refreshTokenManager).saveRefreshTokenInRedis(eq(USER_ID),
                    eq(REFRESH_TOKEN), anyLong());

            assertThatThrownBy(() -> authService.login(SocialType.KAKAO, AUTH_CODE, REDIRECT_URL))
                    .isInstanceOf(AuthRedisException.class);
        }
    }

    @Nested
    @DisplayName("reissue")
    class ReissueTest {

        @Test
        @DisplayName("정상: 토큰 재발급")
        void success() {
            when(jwtProvider.extractUserIdFromToken(REFRESH_TOKEN)).thenReturn(USER_ID);
            when(userRetriever.findUserById(USER_ID)).thenReturn(createUser());
            when(jwtProvider.issueToken(USER_ID, UserRole.USER)).thenReturn(Token.of("new-access", "new-refresh"));
            when(jwtProperties.refreshTokenExpirationTime()).thenReturn(604800000L);

            final TokenDto result = authService.reissue(REFRESH_TOKEN);

            assertThat(result.accessToken()).isEqualTo("new-access");
            assertThat(result.refreshToken()).isEqualTo("new-refresh");
        }

        @Test
        @DisplayName("예외: 잘못된 RF 토큰 → AuthUnAuthorizedException")
        void throwsWhenWrongToken() {
            when(jwtProvider.extractUserIdFromToken(REFRESH_TOKEN)).thenThrow(new AuthWrongJwtException());

            assertThatThrownBy(() -> authService.reissue(REFRESH_TOKEN))
                    .isInstanceOf(AuthUnAuthorizedException.class);
        }

        @Test
        @DisplayName("예외: 만료된 RF 토큰 → AuthUnAuthorizedException")
        void throwsWhenExpired() {
            when(jwtProvider.extractUserIdFromToken(REFRESH_TOKEN)).thenThrow(new AuthExpiredJwtException());

            assertThatThrownBy(() -> authService.reissue(REFRESH_TOKEN))
                    .isInstanceOf(AuthUnAuthorizedException.class);
        }

        @Test
        @DisplayName("예외: Redis 오류 → AuthUnAuthorizedException")
        void throwsWhenRedisError() {
            when(jwtProvider.extractUserIdFromToken(REFRESH_TOKEN)).thenReturn(USER_ID);
            doThrow(new AuthRTException()).when(refreshTokenManager).validateSameWithOriginalRefreshToken(USER_ID,
                    REFRESH_TOKEN);

            assertThatThrownBy(() -> authService.reissue(REFRESH_TOKEN))
                    .isInstanceOf(AuthUnAuthorizedException.class);
        }
    }

    @Nested
    @DisplayName("logout")
    class LogoutTest {

        @Test
        @DisplayName("정상: 로그아웃 → RF 토큰 삭제")
        void success() {
            doNothing().when(refreshTokenManager).validateSameWithOriginalRefreshToken(USER_ID, REFRESH_TOKEN);

            authService.logout(USER_ID, REFRESH_TOKEN);

            verify(refreshTokenManager).deleteRefreshToken(USER_ID);
        }

        @Test
        @DisplayName("예외: RT 미존재 → AuthUnAuthorizedException")
        void throwsWhenRTNotFound() {
            doThrow(new AuthRTNotFoundException()).when(refreshTokenManager)
                    .validateSameWithOriginalRefreshToken(USER_ID, REFRESH_TOKEN);

            assertThatThrownBy(() -> authService.logout(USER_ID, REFRESH_TOKEN))
                    .isInstanceOf(AuthUnAuthorizedException.class);
        }

        @Test
        @DisplayName("예외: Redis 오류 → AuthRedisException")
        void throwsWhenRedisError() {
            doNothing().when(refreshTokenManager).validateSameWithOriginalRefreshToken(USER_ID, REFRESH_TOKEN);
            doThrow(mock(DataAccessException.class)).when(refreshTokenManager).deleteRefreshToken(USER_ID);

            assertThatThrownBy(() -> authService.logout(USER_ID, REFRESH_TOKEN))
                    .isInstanceOf(AuthRedisException.class);
        }
    }
}
