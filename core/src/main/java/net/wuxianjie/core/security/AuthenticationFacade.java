package net.wuxianjie.core.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationFacade {

    public TokenUserDetails getCurrentLoggedInUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            TokenUserDetails anonymous = new TokenUserDetails();

            anonymous.setAccountName(authentication.getName());

            return anonymous;
        }

        return (TokenUserDetails) authentication.getPrincipal();
    }
}
