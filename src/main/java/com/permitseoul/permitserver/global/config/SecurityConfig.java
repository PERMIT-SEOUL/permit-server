package com.permitseoul.permitserver.global.config;

import com.permitseoul.permitserver.global.filter.ExceptionHandlerFilter;
import com.permitseoul.permitserver.global.filter.JwtAuthenticationEntryPoint;
import com.permitseoul.permitserver.auth.jwt.JwtProvider;
import com.permitseoul.permitserver.global.filter.JwtAuthenticationFilter;
import com.permitseoul.permitserver.user.domain.UserRole;
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
            "/api/users/signup"
    };

    private static final String[] adminURIList = {
            "/admin/**"
    };

    private static final String[] authRequiredURIList = {

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
//                        .requestMatchers(authRequiredUrlList).authenticated() // 로그인 필수(todo: 추후에 생기면 주석풀기)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, List.of(whiteURIList)), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class)
                .build();
    }
}


