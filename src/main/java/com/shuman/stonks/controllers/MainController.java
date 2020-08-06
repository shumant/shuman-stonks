package com.shuman.stonks.controllers;

import com.shuman.stonks.twitch.TwitchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
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
import java.util.Map;
import java.util.Optional;

@Controller
public class MainController {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientService clientService;
    private final TwitchApi twitchApi;

    private Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    @Autowired
    public MainController(ClientRegistrationRepository clientRegistrationRepository,
                          OAuth2AuthorizedClientService clientService, TwitchApi twitchApi) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.clientService = clientService;
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
        model.addAttribute("streamer", streamer);
        return "stream";
    }

    @GetMapping(path = "/create-clip")
    public String clip(Model model, @RequestParam String streamer) {
        return twitchApi.userIdFromLogin(streamer)
            .map(twitchApi::createClip)
            .map(uri -> "redirect:" + uri.toString())
            .orElseThrow(() -> new RuntimeException("Can't create a clip"));
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