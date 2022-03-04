package net.wuxianjie.web.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenAttributes {

    /**
     * Token 有效期，单位秒
     */
    public static final int EXPIRES_IN_SECONDS_VALUE = 1800;

    /**
     * JWT 中账号名称字段
     */
    public static final String ACCOUNT_KEY = "account";

    /**
     * JWT 中角色字段
     */
    public static final String ROLE_KEY = "roles";

    /**
     * JWT 中 Token 类型字段
     */
    public static final String TYPE_KEY = "type";

    /**
     * Access Token 类型值
     */
    public static final String TYPE_ACCESS_VALUE = "access";

    /**
     * Refresh Token 类型值
     */
    public static final String TYPE_REFRESH_VALUE = "refresh";
}
