package net.wuxianjie.springbootcore.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Token 身份验证工具类。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtils {

  /**
   * 获取已通过 Token 身份验证后的用户详细数据。
   *
   * <p>
   * 若是开放 API，即无需 Token 身份验证的接口，则返回空。
   * </p>
   *
   * @return 通过 Token 身份验证后的用户详细数据
   */
  public static Optional<TokenUserDetails> getCurrentUser() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
      .map(auth -> {
        // 匿名用户可访问的接口，则返回空
        // auth.getName() 为 anonymous
        if (auth instanceof AnonymousAuthenticationToken) return null;

        return (TokenUserDetails) auth.getPrincipal();
      });
  }
}
