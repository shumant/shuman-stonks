package com.shuman.stonks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/error", "/oauth_login", "/h2-console**").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2Client(oauth2 -> oauth2
                .authorizationCodeGrant(codeGrant -> codeGrant
                    .accessTokenResponseClient(this.accessTokenResponseClient())
                )
            )
            .oauth2Login(oauth2 -> oauth2
                .tokenEndpoint(tokenEndpoint ->
                    tokenEndpoint.accessTokenResponseClient(this.accessTokenResponseClient())
                ).loginPage("/oauth_login")
            );
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient oAuth2Client =
            new DefaultAuthorizationCodeTokenResponseClient();

        OAuth2AccessTokenResponseHttpMessageConverter oAuth2AccessTokenResponseHttpMessageConverter =
            new OAuth2AccessTokenResponseHttpMessageConverter();
        oAuth2AccessTokenResponseHttpMessageConverter.setTokenResponseConverter(
            new TwitchMapOAuth2AccessTokenResponseConverter()
        );

        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
            new FormHttpMessageConverter(),
            oAuth2AccessTokenResponseHttpMessageConverter
        ));

        oAuth2Client.setRestOperations(restTemplate);

        return oAuth2Client;
    }

}
