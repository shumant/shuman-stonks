package com.shuman.stonks.twitch;

import com.fasterxml.jackson.databind.JsonNode;
import com.shuman.stonks.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TwitchApi {

    private final static String URI = "https://api.twitch.tv/helix/";

    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.twitch.client-id}")
    private String clientId;

    public TwitchApi(WebClient webClient) {
        this.webClient = webClient;
    }

    public User getCurrentUser(String accessToken) {
        return webClient.get().uri(URI + "users")
            .header("Authorization", "Bearer " + accessToken)
            .header("Client-ID", clientId)
            .exchange()
            .flatMap(response -> response.bodyToMono(JsonNode.class))
            .map(body -> body.path("data").get(0))
            .map(user -> User.builder()
                .oauthId(user.path("id").asText())
                .name(user.path("display_name").asText())
                .email(user.path("email").asText())
                .thumbnailUrl(user.path("profile_image_url").asText())
                .build()
            ).block();
    }
}
