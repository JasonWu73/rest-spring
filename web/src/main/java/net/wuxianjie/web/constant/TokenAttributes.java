package net.wuxianjie.web.constant;

/**
 * Token 属性常量
 *
 * @author 吴仙杰
 */
public final class TokenAttributes {

  /** Access Token 的有效期 (秒为单位, 有效期30分钟) */
  public static final int TOKEN_EXPIRES_IN_SECONDS = 1800;

  /** JWT 中账号名称 Key */
  public static final String TOKEN_ACCOUNT = "account";

  /** JWT 中类型 Key */
  public static final String TOKEN_TYPE = "type";

  /** Token 类型 Value - Access Token */
  public static final String ACCESS_TOKEN = "access";

  /** Token 类型 Value - Refresh Token */
  public static final String REFRESH_TOKEN = "refresh";

  private TokenAttributes() {}
}
