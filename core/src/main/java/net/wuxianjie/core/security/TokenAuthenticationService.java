package net.wuxianjie.core.security;

public interface TokenAuthenticationService {

    TokenUserDetails authenticate(String accessToken);
}
