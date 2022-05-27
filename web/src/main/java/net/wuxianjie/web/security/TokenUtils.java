package net.wuxianjie.web.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;

import java.util.Map;
import java.util.Optional;

/**
 * 只用于特定包的 Token 工具类。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TokenUtils {

  /**
   * 获取 Token 的类型。
   *
   * @param payload JWT 载荷
   * @return Token 类型
   * @throws TokenAuthenticationException 当 Token 载荷中缺少类型时抛出
   */
  public static String getTokenType(Map<String, Object> payload) throws TokenAuthenticationException {
    return Optional.ofNullable((String) payload.get(TokenAttributes.TOKEN_TYPE_KEY))
      .orElseThrow(() -> new TokenAuthenticationException("Token 缺少 " + TokenAttributes.TOKEN_TYPE_KEY + " 载荷"));
  }

  /**
   * 获取 Token 的用户名。
   *
   * @param payload JWT 载荷
   * @return 用户名
   * @throws TokenAuthenticationException 当 Token 载荷中缺少用户名时抛出
   */
  public static String getUsername(Map<String, Object> payload) throws TokenAuthenticationException {
    return Optional.ofNullable((String) payload.get(TokenAttributes.USERNAME_KEY))
      .orElseThrow(() -> new TokenAuthenticationException("Token 缺少 " + TokenAttributes.TOKEN_TYPE_KEY + " 载荷"));
  }
}
