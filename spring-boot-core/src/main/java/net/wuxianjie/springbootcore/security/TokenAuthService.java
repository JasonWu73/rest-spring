package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;

/**
 * Token 身份验证的业务逻辑接口类。
 *
 * @author 吴仙杰
 */
public interface TokenAuthService {

  /**
   * 执行 Token 身份验证，返回验证通过后的用户详细数据。
   *
   * @param accessToken 需要验证的 Access Token
   * @return 用户详细数据
   * @throws TokenAuthenticationException 当 Token 验证不通过时抛出
   */
  TokenUserDetails authenticate(String accessToken) throws TokenAuthenticationException;
}
