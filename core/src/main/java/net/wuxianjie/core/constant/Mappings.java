package net.wuxianjie.core.constant;

/**
 * 请求路径常量
 *
 * @author 吴仙杰
 */
public final class Mappings {

  /** Spring Boot 白标签错误页请求路径 */
  public static final String ERROR = "/error";

  /** 获取 Access Token */
  public static final String ACCESS_TOKEN = "/access_token";

  /** 刷新 Access Token */
  public static final String REFRESH_TOKEN = "/refresh_token";

  private Mappings() {}
}
