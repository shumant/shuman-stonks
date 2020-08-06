package com.shuman.stonks.controllers;

import com.shuman.stonks.model.Clip;
import com.shuman.stonks.repository.ClipsRepository;
import com.shuman.stonks.repository.UsersRepository;
import com.shuman.stonks.twitch.ClipEditResponse;
import com.shuman.stonks.twitch.TwitchApi;
import com.shuman.stonks.twitch.TwitchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.shuman.stonks.Util.currentUserOauthId;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Controller
public class MainController {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final ClipsRepository clipsRepository;
    private final UsersRepository usersRepository;
    private final TwitchApi twitchApi;

    private Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    @Autowired
    public MainController(ClientRegistrationRepository clientRegistrationRepository,
                          ClipsRepository clipsRepository, UsersRepository usersRepository, TwitchApi twitchApi) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.clipsRepository = clipsRepository;
        this.usersRepository = usersRepository;
        this.twitchApi = twitchApi;
    }

    @PostConstruct
    public void init() {
        Optional.ofNullable(clientRegistrationRepository.findByRegistrationId("twitch"))
            .map(registration ->
                oauth2AuthenticationUrls.put(registration.getClientName(),
                    "oauth2/authorization/" + registration.getRegistrationId()));
    }

    @PostMapping(path = "/stream", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String stream(Model model, @RequestBody MultiValueMap<String, String> formData) {
        final var streamer = formData.getFirst("streamer");
        twitchApi.userIdFromLogin(streamer)
            .orElseThrow(() -> new TwitchException(String.format("Streamer %s not found", streamer)));

        model.addAttribute("streamer", streamer);
        model.addAttribute("clips", twitchApi.clips(clipsIdsForStreamer(streamer)));
        return "stream";
    }

    private List<String> clipsIdsForStreamer(String streamer) {
        return currentUserOauthId()
            .flatMap(usersRepository::findByOauthId)
            .map(user -> clipsRepository.clipsByCreatorAndStreamer(user.getId(), streamer)
                .stream()
                .map(Clip::getId)
                .collect(toList()))
            .orElse(emptyList());
    }

    @GetMapping(path = "/create-clip")
    public String clip(Model model, @RequestParam String streamer) {
        return twitchApi.userIdFromLogin(streamer)
            .flatMap(twitchApi::createClip)
            .map(clip -> {
                persistClip(clip, streamer);
                return "redirect:" + clip.editUrl.toString();
            }).orElseThrow(() -> new TwitchException("Can't create a clip"));
    }

    private void persistClip(ClipEditResponse clipResponse, String streamer) {
        currentUserOauthId()
            .flatMap(usersRepository::findByOauthId)
            .ifPresent(user -> clipsRepository.save(Clip.builder()
                .id(clipResponse.id)
                .creatorId(user.getId())
                .streamerLogin(streamer)
                .viewCount(1)
                .build()));
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/oauth_login")
    public String getLoginPage(Model model) {
        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "oauth_login";
    }
}