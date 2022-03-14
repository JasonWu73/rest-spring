package net.wuxianjie.core.security;

/**
 * Token 认证业务逻辑接口。
 */
public interface TokenAuthenticationService {

  /**
   * 执行 Token 认证。
   */
  TokenUserDetails authenticate(String token);
}
