package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;

/**
 * Token 业务逻辑接口。
 *
 * @author 吴仙杰
 */
public interface TokenService {

  /**
   * 执行 Token 身份验证，返回验证通过后的用户详细数据。
   *
   * @param accessToken 需要验证的 Access Token
   * @return 用户详细数据
   * @throws TokenAuthenticationException 当 Token 验证不通过时抛出
   */
  TokenUserDetails authenticate(String accessToken) throws TokenAuthenticationException;

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
