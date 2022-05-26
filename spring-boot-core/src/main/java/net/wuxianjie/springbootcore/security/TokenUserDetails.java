package net.wuxianjie.springbootcore.security;

/**
 * Token 验证所需的用户详细数据接口。
 *
 * @author 吴仙杰
 * @see AuthUtils
 */
public interface TokenUserDetails {

  /**
   * 获取用户 id。
   *
   * @return Token 的用户 id
   */
  Integer getUserId();

  /**
   * 获取用户名。
   *
   * @return Token 所绑定的用户名
   */
  String getUsername();

  /**
   * 用户绑定的角色，多个角色以英文逗号分隔。
   *
   * @return Token 所绑定的角色
   */
  String getRoles();

  /**
   * 用于 API 鉴权的 Token，在请求头中携带：{@code Authorization: Bearer accessToken}。
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
