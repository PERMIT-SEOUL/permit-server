package com.permitseoul.permitserver.global.external.discord.dto;


public record DiscordMessage(String content) {

    public static DiscordMessage of(final String message) {
        return new DiscordMessage(message);
    }
}
