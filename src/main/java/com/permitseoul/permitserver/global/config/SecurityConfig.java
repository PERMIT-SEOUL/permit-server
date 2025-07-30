package com.permitseoul.permitserver.global.config;

import com.permitseoul.permitserver.global.filter.ExceptionHandlerFilter;
import com.permitseoul.permitserver.global.filter.JwtAuthenticationEntryPoint;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProvider;
import com.permitseoul.permitserver.global.filter.JwtAuthenticationFilter;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    private static final String[] whiteURIList = {
            "/actuator/health",
            "/api/users/signup",
            "/api/users/login",
            "/api/users/reissue",
            "/api/events",
            "/api/events/detail/*",
            "/api/tickets/*",
            "/api/users/email-check"
    };

    private static final String[] adminURIList = {
            "/api/admin/**"
    };

    private static final String[] authRequiredURIList = {
            "/api/users/logout",
            "/api/users",
            "/api/reservations/ready",
            "/api/reservations/ready/*",
            "/api/payments/confirm",
            "/api/payments/cancel",
            "/api/coupons/validate/*"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whiteURIList).permitAll() //로그인 상관 X
                        .requestMatchers(adminURIList).hasRole(UserRole.ADMIN.name())  // ADMIN 권한 필요
                        .requestMatchers(authRequiredURIList).authenticated() // 로그인 필수
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, List.of(whiteURIList)), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class)
                .build();
    }
}


