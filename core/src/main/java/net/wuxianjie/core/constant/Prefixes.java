package net.wuxianjie.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 前缀名常量类
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Prefixes {

  /** 请求头中携带 Access Token 的前缀: {@code Authorization: Bearer {{accessToken}}} */
  public static final String AUTHORIZATION_BEARER = "Bearer ";

  /** Spring Security 角色的前缀 */
  public static final String ROLES = "ROLE_";
}
