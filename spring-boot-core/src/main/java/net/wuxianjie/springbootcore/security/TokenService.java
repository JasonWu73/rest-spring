package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;

/**
 * Access Token 管理的业务逻辑接口类。
 *
 * @author 吴仙杰
 */
public interface TokenService {

  /**
   * 获取 Access Token。
   *
   * @param username 用户名
   * @param password 密码
   * @return {@link TokenData}
   * @throws TokenAuthenticationException 当 Token 身份验证失败时抛出
   */
  TokenData getToken(String username, String password) throws TokenAuthenticationException;

  /**
   * 刷新 Access Token。
   *
   * @param refreshToken 用于刷新的 Token
   * @return {@link TokenData}
   * @throws TokenAuthenticationException 当 Token 身份验证失败时抛出
   */
  TokenData refreshToken(String refreshToken) throws TokenAuthenticationException;
}
