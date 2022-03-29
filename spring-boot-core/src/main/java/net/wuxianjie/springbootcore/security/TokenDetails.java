package net.wuxianjie.springbootcore.security;

/**
 * Token 认证所需的数据接口。
 *
 * @author 吴仙杰
 * @see AuthUtils
 */
public interface TokenDetails {

    /**
     * 获取账号 ID。
     *
     * @return Token 所绑定的账号 ID
     */
    Integer getAccountId();

    /**
     * 获取账号名。
     *
     * @return Token 所绑定的账号名
     */
    String getAccountName();

    /**
     * 获取以英文逗号分隔的角色。
     *
     * @return Token 所绑定的角色
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
