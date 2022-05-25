package net.wuxianjie.web.user;

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
   * 获取 Token 类型。
   *
   * @param payload JWT 载荷
   * @return Token 类型
   */
  public static String getTokenType(Map<String, Object> payload) {
    return Optional.ofNullable((String) payload.get(TokenAttributes.TOKEN_TYPE_KEY))
      .orElseThrow(() -> new TokenAuthenticationException("Token 缺少载荷 [" + TokenAttributes.TOKEN_TYPE_KEY + "]"));
  }

  /**
   * 获取 Token 账号名。
   *
   * @param payload JWT 载荷
   * @return Token 账号名
   */
  public static String getTokenAccount(Map<String, Object> payload) {
    return Optional.ofNullable((String) payload.get(TokenAttributes.USERNAME_KEY))
      .orElseThrow(() -> new TokenAuthenticationException("Token 缺少载荷 [" + TokenAttributes.USERNAME_KEY + "]"));
  }
}
