package com.permitseoul.permitserver.global.filter;

import com.permitseoul.permitserver.domain.auth.core.exception.AuthCookieException;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthExpiredJwtException;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.domain.auth.core.jwt.CookieExtractor;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProvider;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.domain.CookieType;
import com.permitseoul.permitserver.global.exception.FilterException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final List<String> whiteURIList;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String REISSUE_URI = "/api/users/reissue";
    private static final String LOGIN_URI   = "/api/users/login";
    private static final String USER_ID_MDC_KEY = "user_id";
    private static final String ANONYMOUS_USER_ID = "anonymous";

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {
        final String uri = request.getRequestURI();
        try {
            MDC.put(USER_ID_MDC_KEY, ANONYMOUS_USER_ID);

            if(isHealthCheckUri(uri) || isLoginOrReissue(uri)) {
                filterChain.doFilter(request, response);
                return;
            }
            setAuthentication(request);
            filterChain.doFilter(request, response);
        } catch (AuthCookieException e) {
            if(isWhiteListUrl(uri)) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null, null));
                filterChain.doFilter(request, response);
            } else {
                throw new FilterException(ErrorCode.NOT_FOUND_AT_COOKIE);
            }
        } catch (AuthExpiredJwtException e) {
            throw new FilterException(ErrorCode.UNAUTHORIZED_AT_EXPIRED);
        } catch (AuthWrongJwtException e) {
            throw new FilterException(ErrorCode.UNAUTHORIZED);
        } catch (ServletException | IOException e) {
            throw new FilterException(ErrorCode.INTERNAL_FILTER_ERROR);
        } catch (Exception e) {
            throw new FilterException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            MDC.remove(USER_ID_MDC_KEY);
        }
    }

    private void setAuthentication(final HttpServletRequest request) {
        final String token = CookieExtractor.extractCookie(request, CookieType.ACCESS_TOKEN).getValue();
        final long userId = jwtProvider.extractUserIdFromToken(token);
        MDC.put(USER_ID_MDC_KEY, String.valueOf(userId));
        final String userRole = jwtProvider.extractUserRoleFromToken(token);
        final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userRole));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, authorities));
    }

    private boolean isWhiteListUrl(final String requestURI) {
        return whiteURIList.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    private boolean isHealthCheckUri(final String uri) {
        return pathMatcher.match(Constants.HEALTH_CHECK_URL, uri);
    }

    private boolean isLoginOrReissue(final String uri) {
        return pathMatcher.match(LOGIN_URI, uri)
                || pathMatcher.match(REISSUE_URI, uri);
    }
}
