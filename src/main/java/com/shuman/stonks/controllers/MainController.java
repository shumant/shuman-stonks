package com.shuman.stonks.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class MainController {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientService clientService;

    private Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    @Autowired
    public MainController(ClientRegistrationRepository clientRegistrationRepository,
                          OAuth2AuthorizedClientService clientService) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.clientService = clientService;
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
        model.addAttribute("streamer", formData.getFirst("streamer"));
        return "stream";
    }

    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication =
            SecurityContextHolder
                .getContext()
                .getAuthentication();

        OAuth2AuthenticationToken oauthToken =
            (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient client =
            clientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        model.addAttribute("eventName", "FIFA 2018");
        return "index";
    }

    @GetMapping("/oauth_login")
    public String getLoginPage(Model model) {
        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "oauth_login.html";
    }

    @GetMapping("/authorities")
    public Collection<GrantedAuthority> getAuthorities(
        @AuthenticationPrincipal OAuth2AuthenticationToken token
    ) {
        return token.getAuthorities();
    }
}