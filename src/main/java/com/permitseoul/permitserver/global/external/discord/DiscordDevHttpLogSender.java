package com.permitseoul.permitserver.global.external.discord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.permitseoul.permitserver.global.external.discord.dto.DiscordMessage;
import com.permitseoul.permitserver.global.external.discord.util.DiscordMessageFormatterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordDevHttpLogSender implements DiscordSender {

    private final DiscordFeignClient discordFeignClient;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Async("alertExecutor")
    public void send(final String jsonMessage) {
        try {
            final JsonNode root = OBJECT_MAPPER.readTree(jsonMessage);
            final String content = DiscordMessageFormatterUtil.formatDevHttpLogToMarkdown(root);
            discordFeignClient.sendMessage(DiscordMessage.of(content));

        } catch (Exception e) {
            log.error("🚨 [DiscordDevHttpLogSender] Discord 전송 실패", e);
        }
    }
}