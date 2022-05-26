package net.wuxianjie.web.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * JWT 属性名及属性值常量类。
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
   * JWT 载荷属性：用户名。
   */
  public static final String USERNAME_KEY = "username";

  /**
   * JWT 载荷属性：菜单项。
   */
  public static final String MENU_KEY = "menus";

  /**
   * JWT 载荷属性：Token 类型。
   */
  public static final String TOKEN_TYPE_KEY = "type";

  /**
   * JWT 载荷属性值：Access Token。
   */
  public static final String ACCESS_TOKEN_TYPE_VALUE = "access";

  /**
   * JWT 载荷属性值：Refresh Token。
   */
  public static final String REFRESH_TOKEN_TYPE_VALUE = "refresh";
}
