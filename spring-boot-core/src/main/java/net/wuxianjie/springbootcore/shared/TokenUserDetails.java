package net.wuxianjie.springbootcore.shared;

import net.wuxianjie.springbootcore.security.Role;

/**
 * Token 认证所需的用户详细数据接口。
 *
 * @author 吴仙杰
 * @see AuthUtils
 */
public interface TokenUserDetails {

    /**
     * 获取账号 id。
     *
     * @return Token 所绑定的账号 id
     */
    Integer getAccountId();

    /**
     * 获取账号。
     *
     * @return Token 所绑定的账号
     */
    String getAccountName();

    /**
     * 用户绑定的角色，多个角色以英文逗号分隔。
     *
     * @return Token 所绑定的角色
     * @see Role#value()
     */
    String getRoles();

    /**
     * 用于 API 鉴权的 Token，在请求头中携带：`Authorization: Bearer accessToken`。
     *
     * @return Access Token
     */
    String getAccessToken();

    /**
     * 用于刷新鉴权信息的 Token。
     *
     * @return Refresh Token
     */
    String getRefreshToken();
}
