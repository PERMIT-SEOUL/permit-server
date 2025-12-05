package com.permitseoul.permitserver.global.aop.resolver.event;


import com.permitseoul.permitserver.global.exception.UrlSecureException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.SecureUrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventIdPathVariableResolver implements HandlerMethodArgumentResolver {
    private static final String EVENT_ID_PATH_VARIABLE = "eventId";
    private final SecureUrlUtil secureUrlUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(EventIdPathVariable.class);
    }

    @Override
    public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        final String eventId = pathVariables.get(EVENT_ID_PATH_VARIABLE);
        try {
            return secureUrlUtil.decode(eventId);
        } catch (Exception e) {
            throw new UrlSecureException(ErrorCode.BAD_REQUEST_ID_DECODE_ERROR);
        }
    }
}
