package com.permitseoul.permitserver.auth.domain;

import lombok.ToString;

@ToString
public enum TokenType {
    ACCESS_TOKEN,
    REFRESH_TOKEN,
}
