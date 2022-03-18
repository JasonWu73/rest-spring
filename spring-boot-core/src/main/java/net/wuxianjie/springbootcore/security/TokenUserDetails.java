package net.wuxianjie.springbootcore.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 认证后的详细数据。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenUserDetails {

    /**
     * 账号 ID。
     */
    private Integer accountId;

    /**
     * 账号名称。
     */
    private String accountName;

    /**
     * 该账号所拥有的角色。
     */
    private String accountRoles;

    /**
     * Access Token.
     */
    private String accessToken;

    /**
     * Refresh Token.
     */
    private String refreshToken;
}
