package com.shuman.stonks.listener;

import com.shuman.stonks.model.Auth;
import com.shuman.stonks.repository.AuthRepository;
import com.shuman.stonks.repository.UsersRepository;
import com.shuman.stonks.twitch.TwitchApi;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UsersRepository usersRepository;
    private final AuthRepository authRepository;
    private final TwitchApi twitchApi;

    public AuthSuccessListener(UsersRepository usersRepository, AuthRepository authRepository, TwitchApi twitchApi) {
        this.usersRepository = usersRepository;
        this.authRepository = authRepository;
        this.twitchApi = twitchApi;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        final var token = ((OAuth2LoginAuthenticationToken) event.getAuthentication());
        final var twitchUser = twitchApi.getCurrentUser(token.getAccessToken().getTokenValue());

        final var user = usersRepository.findByOauthId(twitchUser.getOauthId())
            .map(existingUser -> existingUser.update(twitchUser))
            .orElseGet(twitchUser::withRandomId);

        usersRepository.save(user);

        final var auth = Auth.builder()
            .userId(user.getId())
            .scopes(String.join(",", token.getAccessToken().getScopes()))
            .accessToken(token.getAccessToken().getTokenValue())
            .refreshToken(token.getRefreshToken().getTokenValue())
            .build();

        authRepository.findByUserId(user.getId())
            .map(existingAuth -> existingAuth.update(auth))
            .or(() -> Optional.of(auth.withRandomId()))
            .ifPresent(authRepository::save);
    }
}
