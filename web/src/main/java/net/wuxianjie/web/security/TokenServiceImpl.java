package net.wuxianjie.web.security;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.SecurityConfig;
import net.wuxianjie.springbootcore.security.TokenData;
import net.wuxianjie.springbootcore.security.TokenService;
import net.wuxianjie.springbootcore.util.JwtUtils;
import net.wuxianjie.web.user.ComUserService;
import net.wuxianjie.web.user.CustomUserDetails;
import net.wuxianjie.web.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Access Token 管理业务逻辑处理类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  private final SecurityConfig securityConfig;
  private final PasswordEncoder passwordEncoder;
  private final Cache<String, CustomUserDetails> tokenCache;
  private final ComUserService comUserService;

  @Override
  public TokenData getToken(String username, String password) throws TokenAuthenticationException {
    // 判断用户是否存在
    User user = comUserService.getUserFromDbMustBeExists(username);

    // 判断用户是否启用
    verifyEnabled(user);

    // 判断密码是否正确
    boolean isMatched = passwordEncoder.matches(password, user.getHashedPassword());
    if (!isMatched) throw new TokenAuthenticationException("用户（" + username + "）密码错误");

    // 生成 JWT
    TokenData token = createToken(user);

    // 将 JWT 存入缓存
    addToCache(user, token);

    return token;
  }

  @Override
  public TokenData refreshToken(String refreshToken) throws TokenAuthenticationException {
    // 验证并解析 JWT
    Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), refreshToken);

    // 判断是否为 Refresh Token
    String tokenType = TokenUtils.getTokenType(payload);
    boolean isRefreshToken = StrUtil.equals(tokenType, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);
    if (!isRefreshToken) throw new TokenAuthenticationException("类型为 " + tokenType + " 的 Token 不可用于刷新鉴权");

    // 判断缓存中是否存在该 Refresh Token
    String username = TokenUtils.getTokenAccount(payload);
    verifyRefreshToken(username, refreshToken);

    // 判断用户是否存在
    User user = comUserService.getUserFromDbMustBeExists(username);

    // 判断用户是否启用
    verifyEnabled(user);

    // 生成 JWT
    TokenData token = createToken(user);

    // 将 JWT 存入缓存
    addToCache(user, token);

    return token;
  }

  private void verifyEnabled(User user) {
    boolean isDisabled = user.getEnabled() != YesOrNo.YES;
    if (isDisabled) throw new TokenAuthenticationException("用户（" + user.getUsername() + "）已被禁用");
  }

  private TokenData createToken(User user) {
    Map<String, Object> payload = new HashMap<>();
    payload.put(TokenAttributes.USERNAME_KEY, user.getUsername());
    payload.put(TokenAttributes.MENU_KEY, user.getMenus());

    String accessToken = createToken(payload, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
    String refreshToken = createToken(payload, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);

    return new TokenData(
      TokenAttributes.EXPIRES_IN_SECONDS_VALUE,
      accessToken,
      refreshToken
    );
  }

  private String createToken(Map<String, Object> payload, String tokenType) {
    payload.put(TokenAttributes.TOKEN_TYPE_KEY, tokenType);
    return JwtUtils.createJwt(
      securityConfig.getJwtSigningKey(),
      payload,
      TokenAttributes.EXPIRES_IN_SECONDS_VALUE
    );
  }

  private void addToCache(User user, TokenData token) {
    CustomUserDetails userDetails = new CustomUserDetails(
      user.getUserId(),
      user.getUsername(),
      user.getMenus(),
      token.getAccessToken(),
      token.getRefreshToken()
    );

    tokenCache.put(user.getUsername(), userDetails);
  }

  private void verifyRefreshToken(String username, String refreshToken) {
    Optional.ofNullable(tokenCache.getIfPresent(username))
      .ifPresentOrElse(
        userDetails -> {
          boolean equalsRefreshToken = StrUtil.equals(refreshToken, userDetails.getRefreshToken());
          if (!equalsRefreshToken) throw new TokenAuthenticationException("用户（" + username + "）Refresh Token 已被刷新");
        },
        () -> {
          throw new TokenAuthenticationException("用户（" + username + "）Refresh Token 已过期");
        }
      );
  }
}
