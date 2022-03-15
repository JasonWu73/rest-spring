package net.wuxianjie.web.user;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.security.SecurityConfigData;
import net.wuxianjie.springbootcore.security.TokenAuthenticationService;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.JwtUtils;
import net.wuxianjie.springbootcore.shared.TokenAuthenticationException;
import net.wuxianjie.web.shared.BeanQualifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationServiceImpl
    implements TokenAuthenticationService {

  @Qualifier(BeanQualifiers.TOKEN_CACHE)
  private final Cache<String, TokenUserDetails> tokenCache;

  private final SecurityConfigData securityConfig;

  @Override
  public TokenUserDetails authenticate(String token) {
    final Map<String, Object> payload = JwtUtils.verifyTwtReturnPayload(
        securityConfig.getJwtSigningKey(), token);

    final String username = (String) payload.get(TokenAttributes.ACCOUNT_KEY);
    final String tokenType =
        (String) payload.get(TokenAttributes.TOKEN_TYPE_KEY);

    if (!Objects.equals(tokenType, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE)) {
      throw new TokenAuthenticationException("Token 类型错误");
    }

    return getUserDetailsFromCache(username, token);
  }

  private TokenUserDetails getUserDetailsFromCache(String username,
                                                   String accessToken) {
    final TokenUserDetails userDetails = tokenCache.getIfPresent(username);

    if (userDetails == null
        || !Objects.equals(accessToken, userDetails.getAccessToken())) {
      throw new TokenAuthenticationException("Token 已过期");
    }

    return userDetails;
  }
}
