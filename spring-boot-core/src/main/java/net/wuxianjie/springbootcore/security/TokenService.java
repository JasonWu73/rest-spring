package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;

/**
 * Access Token 管理业务逻辑接口。
 *
 * @author 吴仙杰
 */
public interface TokenService {

  /**
   * 获取 Access Token。
   *
   * @param account  账号
   * @param password 密码
   * @return {@link TokenData}
   * @throws TokenAuthenticationException 当 Token 认证失败时抛出
   */
  TokenData getToken(String account, String password) throws TokenAuthenticationException;

  /**
   * 刷新 Access Token。
   *
   * @param refreshToken 用于刷新的 Token
   * @return {@link TokenData}
   * @throws TokenAuthenticationException 当 Token 认证失败时抛出
   */
  TokenData refreshToken(String refreshToken) throws TokenAuthenticationException;
}
