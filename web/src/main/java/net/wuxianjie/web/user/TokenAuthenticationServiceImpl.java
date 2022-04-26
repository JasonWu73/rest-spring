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
 * 实现 Token 认证业务逻辑。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

  private final SecurityConfig securityConfig;
  private final Cache<String, UserDetails> tokenCache;

  @Override
  public UserDetails authenticate(final String token) throws TokenAuthenticationException {
    final Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), token);

    final String tokenType = Optional.ofNullable((String) payload.get(TokenAttributes.TOKEN_TYPE_KEY))
      .orElseThrow(() -> new TokenAuthenticationException(
        StrUtil.format("Token 缺少 {} 信息", TokenAttributes.TOKEN_TYPE_KEY)));

    if (!StrUtil.equals(tokenType, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE)) {
      throw new TokenAuthenticationException("非 Access Token");
    }

    final String username = Optional.ofNullable((String) payload.get(TokenAttributes.ACCOUNT_KEY))
      .orElseThrow(() -> new TokenAuthenticationException(
        StrUtil.format("Token 缺少 {} 信息", TokenAttributes.ACCOUNT_KEY)));

    return getUserDetailsFromCache(username, token);
  }

  private UserDetails getUserDetailsFromCache(final String username, final String token) {
    final UserDetails userDetails = tokenCache.getIfPresent(username);

    if (userDetails == null || !StrUtil.equals(token, userDetails.getAccessToken())) {
      throw new TokenAuthenticationException("Token 已过期");
    }

    return userDetails;
  }
}
