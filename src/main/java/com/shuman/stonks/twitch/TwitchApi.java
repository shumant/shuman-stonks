package com.shuman.stonks.twitch;

import com.fasterxml.jackson.databind.JsonNode;
import com.shuman.stonks.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Optional;

import static org.springframework.util.StringUtils.isEmpty;

@Service
public class TwitchApi {

    private final WebClient webClient;

    public TwitchApi(WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<String> userIdFromLogin(String login) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("users").queryParam("login", login).build())
            .exchange()
            .flatMap(response -> response.bodyToMono(JsonNode.class))
            .map(body -> body.path("data").path(0))
            .map(user -> Optional.ofNullable(user.path("id").asText()).filter(str -> !isEmpty(str)))
            .block();
    }

    public URI createClip(String broadcasterId) {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path("clips").queryParam("broadcaster_id", broadcasterId).build())
            .exchange()
            .flatMap(response -> response.bodyToMono(JsonNode.class))
            .map(body -> body.path("data").path(0))
            .map(clip -> clip.path("edit_url").asText())
            .map(URI::create)
            .block();
    }

    public User getCurrentUser(String accessToken) {
        return webClient.get().uri( "users")
            .header("Authorization", "Bearer " + accessToken)
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
