package com.shuman.stonks;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

public class Util {

    public static Optional<String> currentUserOauthId() {
        return Optional.ofNullable(((OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getAttribute("sub"))
            .map(Object::toString);

    }
}
