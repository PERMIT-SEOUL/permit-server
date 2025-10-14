package com.permitseoul.permitserver.global.external.discord.client;

import com.permitseoul.permitserver.global.config.FeignConfig;
import com.permitseoul.permitserver.global.external.discord.dto.DiscordMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "discordClient",
        url = "${discord.webhook-url}",
        configuration = FeignConfig.class
)
public interface DiscordFeignClient {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    void sendMessage(@RequestBody DiscordMessage discordMessage);
}