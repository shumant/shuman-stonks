package com.shuman.stonks.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class MainController {
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    private Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    @PostConstruct
    public void init() {
        Optional.ofNullable(clientRegistrationRepository.findByRegistrationId("twitch"))
            .map(registration ->
            oauth2AuthenticationUrls.put(registration.getClientName(),
                "oauth2/authorization/" + registration.getRegistrationId()));
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