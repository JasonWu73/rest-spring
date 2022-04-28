package net.wuxianjie.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.security.TokenUserDetails;

/**
 * Token 认证后的用户详细数据。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails implements TokenUserDetails {

    /**
     * 用户 id。
     */
    private Integer accountId;

    /**
     * 用户名。
     */
    private String accountName;

    /**
     * 用户绑定的角色，多个角色以英文逗号分隔。
     *
     * @see Role#value()
     */
    private String roles;

    /**
     * 用于 API 鉴权的 Token，在请求头中携带：{@code Authorization: Bearer accessToken}。
     */
    private String accessToken;

    /**
     * 用于刷新鉴权信息的 Token。
     */
    private String refreshToken;
}
