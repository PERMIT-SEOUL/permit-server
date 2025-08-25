package com.permitseoul.permitserver.global.resolver.user;

import com.permitseoul.permitserver.global.exception.ResolverException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@Component
public class UserIdHeaderResolver implements HandlerMethodArgumentResolver {

    private static final String ANONY_USER = "anonymousUser";
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterType().equals(Long.class) || parameter.getParameterType().equals(long.class))
                && parameter.hasParameterAnnotation(UserIdHeader.class);
    }

    @Override
    public Long resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        final Authentication authentication = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .orElseThrow(() -> new ResolverException(ErrorCode.UNAUTHORIZED_SECURITY_ENTRY));
        final Object principal = authentication.getPrincipal();

        if (principal == null || principal.equals(ANONY_USER)) {
            throw new ResolverException(ErrorCode.UNAUTHORIZED_PRINCIPLE);
        }
        return (Long) principal;
    }
}
