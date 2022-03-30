package net.wuxianjie.web.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * JWT 相关的属性名及属性值。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenAttributes {

    /**
     * JWT 的过期时间，单位秒。
     */
    public static final int EXPIRES_IN_SECONDS_VALUE = 1800;

    /**
     * JWT 载荷属性：账号名。
     */
    public static final String ACCOUNT_KEY = "account";

    /**
     * JWT 载荷属性：角色。
     */
    public static final String ROLE_KEY = "roles";

    /**
     * JWT 载荷属性：Token 类型。
     */
    public static final String TOKEN_TYPE_KEY = "type";

    /**
     * JWT 载荷属性的 Access Token 类型值。
     */
    public static final String ACCESS_TOKEN_TYPE_VALUE = "access";

    /**
     * JWT 载荷属性的 Refresh Token 类型的值。
     */
    public static final String REFRESH_TOKEN_TYPE_VALUE = "refresh";
}
