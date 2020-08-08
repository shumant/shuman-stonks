package com.shuman.stonks.twitch;

import com.fasterxml.jackson.databind.JsonNode;
import com.shuman.stonks.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Spliterators;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
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
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(body -> body.path("data").path(0))
            .map(user -> Optional.ofNullable(user.path("id").asText()).filter(str -> !isEmpty(str)))
            .onErrorReturn(empty())
            .block();
    }

    public Optional<ClipEditResponse> createClip(String broadcasterId) {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path("clips").queryParam("broadcaster_id", broadcasterId).build())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(body -> body.path("data").path(0))
            .filter(clip -> !clip.isMissingNode())
            .map(clip -> Optional.of(new ClipEditResponse(clip.path("id").asText(), URI.create(clip.path("edit_url").asText()))))
            .onErrorReturn(empty())
            .block();
    }

    public List<ClipResponse> clips(List<String> clipIds) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("clips").queryParam("id", clipIds).build())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(body -> {
                int f = 5;
                return body.path("data");
            })
            .filter(JsonNode::isArray)
            .map(this::fromJsonArray)
            .onErrorReturn(emptyList())
            .block();

    }

    private List<ClipResponse> fromJsonArray(JsonNode array) {
        return stream(Spliterators.spliteratorUnknownSize(array.elements(), 0), false)
            .map(node -> new ClipResponse(node.path("title").asText(),
                node.path("id").asText(),
                URI.create(node.path("url").asText()),
                node.path("view_count").asLong()))
            .collect(toList());
    }

    public User getCurrentUser(String accessToken) {
        return webClient.get().uri("users")
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(JsonNode.class)
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
