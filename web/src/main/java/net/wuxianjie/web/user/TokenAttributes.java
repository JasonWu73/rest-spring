package net.wuxianjie.web.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenAttributes {

    public static final int EXPIRES_IN_SECONDS_VALUE = 1800;

    public static final String ACCOUNT_KEY = "account";

    public static final String ROLE_KEY = "roles";

    public static final String TOKEN_TYPE_KEY = "type";

    public static final String ACCESS_TOKEN_TYPE_VALUE = "access";

    public static final String REFRESH_TOKEN_TYPE_VALUE = "refresh";
}
