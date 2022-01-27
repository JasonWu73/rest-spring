package net.wuxianjie.web.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.constant.BeanQualifiers;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.model.dto.CachedTokenDto;
import net.wuxianjie.core.service.TokenAuthenticationService;
import net.wuxianjie.core.util.JwtUtils;
import net.wuxianjie.web.constant.TokenAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 基于本地缓存实现 Token 鉴权认证
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

  @Qualifier(BeanQualifiers.JWT_SIGNING_KEY) private final String jwtSigningKey;
  private final Cache<String, CachedTokenDto> tokenCache;

  @Override
  public CachedTokenDto authenticate(@NonNull final String accessToken) {
    // 解析 Token
    final Map<String, Object> payload = JwtUtils.verifyAndParseToken(jwtSigningKey, accessToken);
    final String username = (String) payload.get(TokenAttributes.TOKEN_ACCOUNT);

    // 获取缓存中的 Access Token
    final CachedTokenDto tokenDto = tokenCache.getIfPresent(username);

    // 核验缓存中的 Access Token 与传入的 Access Token
    if (tokenDto == null || !tokenDto.getAccessToken().equals(accessToken)) {
      throw new TokenAuthenticationException("Token 已过期");
    }

    return tokenDto;
  }
}
