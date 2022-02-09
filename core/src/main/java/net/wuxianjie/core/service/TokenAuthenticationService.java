package net.wuxianjie.core.service;

import net.wuxianjie.core.domain.CachedToken;

/**
 * Token鉴权认证机制业务逻辑接口
 *
 * @author 吴仙杰
 */
public interface TokenAuthenticationService {

  /**
   * 执行具体的Token认证业务
   *
   * @param accessToken Access Token
   * @return 认证后的Token数据
   */
  CachedToken authenticate(final String accessToken);
}
