package net.wuxianjie.web.constant;

/**
 * 请求路径常量
 *
 * @author 吴仙杰
 */
public final class Mappings {

  /** 匿名用户即可访问的测试资源 */
  public static final String ANONYMOUS = "/anonymous";

  /** 只要身份认证通过的用户都可以访问的测试资源 */
  public static final String GUEST = "/guest";

  /** 拥有 {@code user} 角色的用户才可以访问的测试资源 */
  public static final String USER = "/user";

  /** 拥有 {@code admin} 角色的用户才可以访问的测试资源 */
  public static final String ADMIN = "/admin";

  private Mappings() {}
}
