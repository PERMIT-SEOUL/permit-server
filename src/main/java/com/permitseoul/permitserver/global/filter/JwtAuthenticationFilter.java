package com.permitseoul.permitserver.global.filter;

import com.permitseoul.permitserver.auth.jwt.CookieExtractor;
import com.permitseoul.permitserver.auth.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String token = CookieExtractor.getTokenCookie(request).getValue();
        final long userId = jwtProvider.extractUserIdFromToken(token);
        final String userRole = jwtProvider.extractUserRoleFromToken(token);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userRole));

        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(userId, null, authorities));

        filterChain.doFilter(request, response);
    }
}

