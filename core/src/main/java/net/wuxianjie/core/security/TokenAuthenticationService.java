package net.wuxianjie.core.security;

import org.springframework.lang.NonNull;

/**
 * Token 认证业务逻辑接口。
 */
public interface TokenAuthenticationService {

    /**
     * 执行 Token 认证。
     */
    @NonNull
    TokenUserDetails authenticate(@NonNull String accessToken);
}
