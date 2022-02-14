package net.wuxianjie.web.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 与Token属性相关的常量类
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenAttributes {

  /** Access Token的有效期（以秒为单位，有效期30分钟） */
  public static final int TOKEN_EXPIRES_IN_SECONDS = 1800;

  /** JWT中账号名称Key */
  public static final String TOKEN_ACCOUNT = "account";

  /**
   * JWT中角色Key
   */
  public static final String TOKEN_ROLE = "roles";

  /** JWT中Token类型Key */
  public static final String TOKEN_TYPE = "type";

  /** Token类型Value - Access Token */
  public static final String ACCESS_TOKEN = "access";

  /** Token类型Value - Refresh Token */
  public static final String REFRESH_TOKEN = "refresh";
}
