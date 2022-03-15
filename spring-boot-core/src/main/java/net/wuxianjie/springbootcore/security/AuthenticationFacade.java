package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.shared.InternalServerException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 封装获取 Spring Security 认证过后的用户详细数据的方法，
 * 使其可通过依赖注入方便地使用。
 */
@Service
public class AuthenticationFacade {

  /**
   * 获取当前已登录的用户详细数据。
   */
  public TokenUserDetails getCurrentUser() {
    final Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof AnonymousAuthenticationToken) {
      final TokenUserDetails anonymous = new TokenUserDetails();

      anonymous.setAccountName(authentication.getName());

      return anonymous;
    }

    final TokenUserDetails userDetails =
        (TokenUserDetails) authentication.getPrincipal();

    if (userDetails == null) {
      throw new InternalServerException("无法获取已登录用户的详细数据");
    }

    return userDetails;
  }
}
