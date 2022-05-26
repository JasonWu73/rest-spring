package net.wuxianjie.springbootcore.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Token 认证工具类。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUtils {

  /**
   * 获取已通过 Token 认证后的用户详细数据。
   * <p>
   * 若是开放 API，即无需 Token 认证的接口，则返回空。
   * </p>
   *
   * @return 通过 Token 认证后的用户详细数据
   */
  public static Optional<TokenUserDetails> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    return Optional.ofNullable(authentication)
      .map(auth -> {
        // 匿名用户可访问的接口，则返回空
        // auth.getName() 为 anonymous
        if (auth instanceof AnonymousAuthenticationToken) return null;

        return (TokenUserDetails) auth.getPrincipal();
      });
  }
}
