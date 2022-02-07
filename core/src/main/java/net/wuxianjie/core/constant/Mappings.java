package net.wuxianjie.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 请求路径常量
 *
 * @author 吴仙杰
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Mappings {

  /** 获取 Access Token */
  public static final String ACCESS_TOKEN = "/access_token";

  /** 刷新 Access Token */
  public static final String REFRESH_TOKEN = "/refresh_token";
}
