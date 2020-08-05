package com.shuman.stonks.listener;

import com.shuman.stonks.repository.UsersRepository;
import com.shuman.stonks.twitch.TwitchApi;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UsersRepository usersRepository;
    private final TwitchApi twitchApi;

    public AuthSuccessListener(UsersRepository usersRepository, TwitchApi twitchApi) {
        this.usersRepository = usersRepository;
        this.twitchApi = twitchApi;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        final var token = ((OAuth2LoginAuthenticationToken) event.getAuthentication()).getAccessToken().getTokenValue();
        final var twitchUser = twitchApi.getCurrentUser(token);

        usersRepository.findByOauthId(twitchUser.getOauthId()).ifPresentOrElse(
            user -> usersRepository.save(user.update(user)),
            () -> usersRepository.save(twitchUser.withRandomId()));
    }
}
