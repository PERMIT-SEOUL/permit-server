package com.permitseoul.permitserver.domain.auth.api.controller;

import com.permitseoul.permitserver.domain.auth.api.dto.LoginRequest;
import com.permitseoul.permitserver.domain.auth.api.dto.TokenDto;
import com.permitseoul.permitserver.domain.auth.api.dto.SignUpRequest;
import com.permitseoul.permitserver.domain.auth.core.jwt.CookieCreatorUtil;
import com.permitseoul.permitserver.domain.auth.api.service.AuthService;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProperties;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.aop.resolver.user.UserIdHeader;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtProperties jwtProperties;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<?>> signUp(
            @RequestBody @Valid final SignUpRequest signUpRequest,
            final HttpServletResponse response
    ) {
        final TokenDto tokenDto = authService.signUp(
                signUpRequest.userName(),
                signUpRequest.userAge(),
                signUpRequest.userGender(),
                signUpRequest.userEmail(),
                signUpRequest.socialType(),
                signUpRequest.socialAccessToken()
        );
        return responseWithGeneratedCookie(response, tokenDto);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(
            @RequestBody @Valid final LoginRequest loginRequest,
            final HttpServletResponse response
    ) {
        final TokenDto tokenDto = authService.login(loginRequest.socialType(), loginRequest.authorizationCode(), loginRequest.redirectUrl());
        return responseWithGeneratedCookie(response, tokenDto);
    }

    //jwt 재발급
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<?>> reissue(
            @CookieValue(name = Constants.REFRESH_TOKEN) Cookie refreshCookie,
            final HttpServletResponse response
    ) {
        final TokenDto tokenDto = authService.reissue(refreshCookie.getValue());
        return responseWithGeneratedCookie(response, tokenDto);
    }
    
    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<?>> logout(
            @UserIdHeader final Long userId,
            @CookieValue(name = Constants.REFRESH_TOKEN) final Cookie refreshTokenCookie,
            final HttpServletResponse response
    ) {
        authService.logout(userId, refreshTokenCookie.getValue());
        // 쿠키 삭제
        final ResponseCookie deleteAccessToken = CookieCreatorUtil.deleteAccessTokenCookie();
        final ResponseCookie deleteRefreshToken = CookieCreatorUtil.deleteRefreshTokenCookie();
        response.setHeader(Constants.SET_COOKIE, deleteAccessToken.toString());
        response.addHeader(Constants.SET_COOKIE, deleteRefreshToken.toString());
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    private ResponseEntity<BaseResponse<?>> responseWithGeneratedCookie(final HttpServletResponse response,
                                                                        final TokenDto tokenDto) {
        final ResponseCookie accessTokenCookie = CookieCreatorUtil.createAccessTokenCookie(tokenDto.accessToken(), jwtProperties.accessTokenExpirationTime());
        final ResponseCookie refreshTokenCookie = CookieCreatorUtil.createRefreshTokenCookie(tokenDto.refreshToken(), jwtProperties.refreshTokenExpirationTime());
        response.setHeader(Constants.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(Constants.SET_COOKIE, refreshTokenCookie.toString());

        return ApiResponseUtil.success(SuccessCode.CREATED);
    }
}
