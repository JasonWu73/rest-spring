package net.wuxianjie.springbootcore.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 认证后的用户详细数据。
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
     * 用于 API 鉴权的 Token，在请求头中携带：`Authorization: Bearer accessToken`。
     */
    private String accessToken;

    /**
     * 用于刷新鉴权信息的 Token。
     */
    private String refreshToken;
}
