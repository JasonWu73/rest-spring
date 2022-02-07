package net.wuxianjie.core.service;

import lombok.NonNull;
import net.wuxianjie.core.domain.CachedToken;

/**
 * Token 鉴权认证机制业务逻辑接口
 *
 * @author 吴仙杰
 */
public interface TokenAuthenticationService {

  /**
   * 执行具体的 Token 认证业务
   *
   * @param accessToken Access Token
   * @return 认证后的 Token 数据
   */
  CachedToken authenticate(@NonNull final String accessToken);
}
