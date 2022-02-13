package net.wuxianjie.core.service.impl;

import net.wuxianjie.core.model.CachedToken;
import net.wuxianjie.core.service.AuthenticationFacade;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 身份认证后的业务操作
 *
 * @author 吴仙杰
 * @see <a href="https://www.baeldung.com/get-user-in-spring-security">Retrieve User Information in Spring Security | Baeldung</a>
 */
@Service
public class AuthenticationFacadeImpl implements AuthenticationFacade {

  @Override
  public CachedToken getCacheToken() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof AnonymousAuthenticationToken) {
      final CachedToken anonymousToken = new CachedToken();
      anonymousToken.setAccountName(authentication.getName());
      return anonymousToken;
    }

    return (CachedToken) authentication.getPrincipal();
  }
}
