package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.security.SecurityConfig;
import net.wuxianjie.springbootcore.security.TokenAuthenticationService;
import net.wuxianjie.springbootcore.util.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Token 认证业务逻辑实现类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

  private final SecurityConfig securityConfig;
  private final Cache<String, CustomUserDetails> tokenCache;

  @Override
  public CustomUserDetails authenticate(String token) throws TokenAuthenticationException {
    Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), token);

    String tokenType = TokenUtils.getTokenType(payload);

    if (!StrUtil.equals(tokenType, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE)) {
      throw new TokenAuthenticationException("仅 Access Token 才可访问");
    }

    String username = TokenUtils.getTokenAccount(payload);

    return getUserDetailsFromCache(username, token);
  }

  private CustomUserDetails getUserDetailsFromCache(String username, String token) {
    return Optional.ofNullable(tokenCache.getIfPresent(username))
      .map(u -> {
        if (!StrUtil.equals(token, u.getAccessToken())) {
          throw new TokenAuthenticationException("Token 已弃用");
        }

        return u;
      })
      .orElseThrow(() -> new TokenAuthenticationException("Token 已过期"));
  }
}
