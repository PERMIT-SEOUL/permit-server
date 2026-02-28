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

        private static final String[] whiteURIListNotUsingToken = {
                "/actuator/health",
                "/api/users/signup",
                "/api/users/login",
                "/api/users/reissue",
                "/api/events",
                "/api/events/detail/*",
                "/api/users/email-check",
                "/api/tickets/info/*",
                "/api/tickets/door/staff/confirm",
                "/api/tickets/door/validation/*",
                "/api/notion/**",
                "/api/guests/**",
                "/api/events/*/sitemap",
        };

        private static final String[] whiteURIListUsingToken = {
                "/api/events/*/timetables", // userId 있으면 개인화
                "/api/events/timetables/*", // userId 있으면 개인화
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
                "/api/coupons/validate/*",
                "/api/events/timetables/likes/*",
                "/api/tickets/user"
        };

        private static final String[] staffURIList = {
                "/api/staff/**"
        };

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity
                        .csrf(AbstractHttpConfigurer::disable)
                        .formLogin(AbstractHttpConfigurer::disable)
                        .httpBasic(AbstractHttpConfigurer::disable)
                        .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers(adminURIList).hasRole(UserRole.ADMIN.name()) // ADMIN// 권한 필요
                                .requestMatchers(staffURIList).hasAnyRole(UserRole.STAFF.name(), UserRole.ADMIN.name()) // staff 권한 이상
                                .requestMatchers(authRequiredURIList).authenticated() // 로그인 필수
                                .requestMatchers(whiteURIListNotUsingToken).permitAll() // 로그인 상관 X + AccessToken 사용X
                                .requestMatchers(whiteURIListUsingToken).permitAll() // 로그인 상관 X + AccessToken 있으면 사용
                                .anyRequest().denyAll())
                        .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, List.of(whiteURIListNotUsingToken), List.of(whiteURIListUsingToken)), UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class)
                        .build();
        }
}
