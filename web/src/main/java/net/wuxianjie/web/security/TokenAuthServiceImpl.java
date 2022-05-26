package net.wuxianjie.web.security;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.security.SecurityConfig;
import net.wuxianjie.springbootcore.security.TokenAuthService;
import net.wuxianjie.springbootcore.util.JwtUtils;
import net.wuxianjie.web.user.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Token 身份验证的业务逻辑处理类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenAuthServiceImpl implements TokenAuthService {

  private final SecurityConfig securityConfig;
  private final Cache<String, CustomUserDetails> tokenCache;

  @Override
  public CustomUserDetails authenticate(String accessToken) throws TokenAuthenticationException {
    // 验证并解析 JWT
    Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), accessToken);

    // 判断是否为 Access Token
    String tokenType = TokenUtils.getTokenType(payload);
    boolean isAccessToken = StrUtil.equals(tokenType, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
    if (!isAccessToken) throw new TokenAuthenticationException("类型为 " + tokenType + " 的 Token 不可用于 API 鉴权");

    // 从缓存中获取用户数据
    String username = TokenUtils.getTokenAccount(payload);
    return getUserDetailsFromCache(username, accessToken);
  }

  private CustomUserDetails getUserDetailsFromCache(String username, String accessToken) {
    return Optional.ofNullable(tokenCache.getIfPresent(username))
      .map(userDetails -> {
        boolean equalsAccessToken = StrUtil.equals(accessToken, userDetails.getAccessToken());
        if (!equalsAccessToken) throw new TokenAuthenticationException("用户（" + username + "）Access Token 已被刷新");

        return userDetails;
      })
      .orElseThrow(() -> new TokenAuthenticationException("用户（" + username + "）Access Token 已过期"));
  }
}
