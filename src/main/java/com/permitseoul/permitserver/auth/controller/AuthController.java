package com.permitseoul.permitserver.auth.controller;

import com.permitseoul.permitserver.auth.dto.TokenDto;
import com.permitseoul.permitserver.auth.dto.SignUpRequest;
import com.permitseoul.permitserver.auth.service.AuthService;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
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

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<?>> login(
            @RequestBody @Valid final SignUpRequest signUpRequest,
            HttpServletResponse response
    ) {
        final TokenDto tokenDto = authService.signUp(
                signUpRequest.userName(),
                signUpRequest.userAge(),
                signUpRequest.userSex(),
                signUpRequest.userEmail(),
                signUpRequest.socialType(),
                signUpRequest.authorizationCode(),
                signUpRequest.redirectUrl()
        );

        final ResponseCookie accessTokenCookie = ResponseCookie.from(Constants.ACCESS_TOKEN, tokenDto.accessToken())
                .maxAge(365L * 24 * 60 * 60) ///todo: 추후에 변경
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
        final ResponseCookie refreshTokenCookie = ResponseCookie.from(Constants.REFRESH_TOKEN, tokenDto.refreshToken())
                .maxAge(365L * 24 * 60 * 60) ///todo: 추후에 변경
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity
                .status(SuccessCode.CREATED.getCode())
                .build();
    }
}
