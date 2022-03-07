package net.wuxianjie.core.rest.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthPrefixes {

    /**
     * 请求头中携带 Access Token 的前缀：{@code Authorization: Bearer {{accessToken}}}
     */
    public static final String AUTHORIZATION_BEARER = "Bearer ";

    /**
     * Spring Security 角色的前缀
     */
    public static final String ROLE = "ROLE_";
}
