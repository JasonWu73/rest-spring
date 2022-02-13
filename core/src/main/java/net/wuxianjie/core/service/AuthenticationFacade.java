package net.wuxianjie.core.service;

import net.wuxianjie.core.model.CachedToken;

/**
 * 身份认证后的业务操作
 *
 * @author 吴仙杰
 * @see <a href="https://www.baeldung.com/get-user-in-spring-security">Retrieve User Information in Spring Security | Baeldung</a>
 */
public interface AuthenticationFacade {

  /**
   * 获取缓存中的Token数据
   *
   * @return 缓存中的Token数据
   */
  CachedToken getCacheToken();
}
