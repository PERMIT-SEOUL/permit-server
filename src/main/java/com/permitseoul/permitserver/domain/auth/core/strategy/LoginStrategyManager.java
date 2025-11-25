package com.permitseoul.permitserver.domain.auth.core.strategy;

import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LoginStrategyManager {

    private final Map<SocialType, LoginStrategy> strategyMap;

    public LoginStrategyManager(final List<LoginStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(LoginStrategy::getSocialType, Function.identity() ));
    }

    public LoginStrategy getStrategy(final SocialType socialType) {
        return strategyMap.get(socialType);
    }
}
