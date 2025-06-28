package com.permitseoul.permitserver.auth.controller;

import com.permitseoul.permitserver.auth.dto.CookieDto;
import com.permitseoul.permitserver.auth.dto.LoginRequest;
import com.permitseoul.permitserver.auth.dto.SignUpRequest;
import com.permitseoul.permitserver.auth.service.AuthService;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessBaseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
            @RequestBody final SignUpRequest signUpRequest,
            HttpServletResponse response
    ) {
        final CookieDto cookieDto = authService.signUp(
                signUpRequest.userName(),
                signUpRequest.userAge(),
                signUpRequest.userSex(),
                signUpRequest.userEmail(),
                signUpRequest.socialType(),
                signUpRequest.authorizationCode(),
                signUpRequest.redirectUrl()
        );
        return ResponseEntity
                .status(SuccessBaseCode.CREATED.getStatus())
                .build();
    }
}
