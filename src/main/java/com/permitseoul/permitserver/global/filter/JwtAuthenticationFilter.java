package com.permitseoul.permitserver.global.filter;

import com.permitseoul.permitserver.auth.exception.AuthExpiredJwtException;
import com.permitseoul.permitserver.auth.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.auth.jwt.CookieExtractor;
import com.permitseoul.permitserver.auth.jwt.JwtProvider;
import com.permitseoul.permitserver.global.exception.PermitUnAuthorizedException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String ROLE = "ROLE_";
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            final String token = CookieExtractor.getTokenCookie(request).getValue();
            final long userId = jwtProvider.extractUserIdFromToken(token);
            final String userRole = jwtProvider.extractUserRoleFromToken(token);
            final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(ROLE + userRole));

            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(userId, null, authorities));
            filterChain.doFilter(request, response);
        } catch (AuthExpiredJwtException e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_AT_EXPIRED);
        } catch (AuthWrongJwtException e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED);
        } catch (Exception e) {
            throw new PermitUnAuthorizedException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

