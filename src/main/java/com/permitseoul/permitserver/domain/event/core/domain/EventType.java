package com.permitseoul.permitserver.domain.event.core.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    PERMIT("PERMIT"),
    CEILING("ceiling service"),
    OLYMPAN("Olympan");

    private final String displayName;
}
