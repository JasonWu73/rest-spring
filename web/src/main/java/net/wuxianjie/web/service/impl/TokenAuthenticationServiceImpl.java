package net.wuxianjie.web.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.constant.BeanQualifiers;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.model.CachedToken;
import net.wuxianjie.core.service.TokenAuthenticationService;
import net.wuxianjie.core.util.JwtUtils;
import net.wuxianjie.web.constant.TokenAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 基于本地缓存实现Token鉴权认证
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

  @Qualifier(BeanQualifiers.JWT_SIGNING_KEY) private final String jwtSigningKey;
  private final Cache<String, CachedToken> tokenCache;

  @Override
  public CachedToken authenticate(@NonNull final String accessToken) {
    // 解析Token
    final Map<String, Object> payload = JwtUtils.verifyAndParseToken(jwtSigningKey, accessToken);
    final String username = (String) payload.get(TokenAttributes.TOKEN_ACCOUNT);

    // 获取缓存中的Access Token
    final CachedToken tokenDto = tokenCache.getIfPresent(username);

    // 核验缓存中的Access Token与传入的Access Token
    if (tokenDto == null || !tokenDto.getAccessToken().equals(accessToken)) {
      throw new TokenAuthenticationException("Token已过期");
    }

    return tokenDto;
  }
}
