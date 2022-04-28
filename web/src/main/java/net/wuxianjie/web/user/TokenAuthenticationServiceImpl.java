package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.security.SecurityConfig;
import net.wuxianjie.springbootcore.security.TokenAuthenticationService;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
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
  private final Cache<String, UserDetails> tokenCache;

  @Override
  public UserDetails authenticate(String token) throws TokenAuthenticationException {
    Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), token);

    String tokenType = Optional.ofNullable((String) payload.get(TokenAttributes.TOKEN_TYPE_KEY))
      .orElseThrow(() -> new TokenAuthenticationException("Token 缺少 " + TokenAttributes.TOKEN_TYPE_KEY + " 信息"));

    if (!StrUtil.equals(tokenType, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE)) {
      throw new TokenAuthenticationException("非 Access Token 不可用于访问");
    }

    String username = Optional.ofNullable((String) payload.get(TokenAttributes.ACCOUNT_KEY))
      .orElseThrow(() -> new TokenAuthenticationException("Token 缺少 " + TokenAttributes.ACCOUNT_KEY + " 信息"));

    return getUserDetailsFromCache(username, token);
  }

  private UserDetails getUserDetailsFromCache(String username, String token) {
    return Optional.ofNullable(tokenCache.getIfPresent(username))
      .map(userDetails -> {
        if (!StrUtil.equals(token, userDetails.getAccessToken())) {
          throw new TokenAuthenticationException("Token 已弃用");
        }

        return userDetails;
      })
      .orElseThrow(() -> new TokenAuthenticationException("Token 已过期"));
  }
}
