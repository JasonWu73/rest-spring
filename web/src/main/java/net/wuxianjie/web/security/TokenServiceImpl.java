package net.wuxianjie.web.security;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.SecurityPropertiesConfig;
import net.wuxianjie.springbootcore.security.TokenData;
import net.wuxianjie.springbootcore.security.TokenService;
import net.wuxianjie.springbootcore.util.JwtUtils;
import net.wuxianjie.web.user.CustomUserDetails;
import net.wuxianjie.web.user.User;
import net.wuxianjie.web.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Access Token 业务逻辑实现类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  private final SecurityPropertiesConfig securityConfig;
  private final PasswordEncoder passwordEncoder;
  private final Cache<String, CustomUserDetails> tokenCache;
  private final UserService userService;

  @Override
  public CustomUserDetails authenticate(String accessToken) throws TokenAuthenticationException {
    // 验证并解析 JWT
    Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), accessToken);

    // 检查是否为 Access Token
    String tokenType = TokenUtils.getTokenType(payload);
    boolean isAccessToken = StrUtil.equals(tokenType, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
    if (!isAccessToken) throw new TokenAuthenticationException(StrUtil.format("该 Token 不可用于 API 鉴权 [{}]", tokenType));

    // 从缓存中获取用户数据
    String username = TokenUtils.getUsername(payload);
    return getUserFromCache(username, accessToken);
  }

  @Override
  public TokenData getToken(String username, String password) throws TokenAuthenticationException {
    // 检查用户是否存在
    User user = userService.getUserFromDatabaseMustBeExists(username);

    // 检查用户是否启用
    checkForUserIsEnabled(user);

    // 检查密码是否正确
    boolean isMatched = passwordEncoder.matches(password, user.getHashedPassword());
    if (!isMatched) throw new TokenAuthenticationException("密码错误");

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

    // 检查是否为 Refresh Token
    String tokenType = TokenUtils.getTokenType(payload);
    boolean isRefreshToken = StrUtil.equals(tokenType, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);
    if (!isRefreshToken) throw new TokenAuthenticationException(StrUtil.format("该 Token 不可用于刷新 [{}]", tokenType));

    // 检查缓存中是否存在该 Refresh Token
    String username = TokenUtils.getUsername(payload);
    checkForRefreshTokenExists(username, refreshToken);

    // 检查用户是否存在
    User user = userService.getUserFromDatabaseMustBeExists(username);

    // 检查用户是否启用
    checkForUserIsEnabled(user);

    // 生成 JWT
    TokenData token = createToken(user);

    // 将 JWT 存入缓存
    addToCache(user, token);

    return token;
  }

  private CustomUserDetails getUserFromCache(String username, String accessToken) {
    return Optional.ofNullable(tokenCache.getIfPresent(username))
      .map(userDetails -> {
        boolean equalsAccessToken = StrUtil.equals(accessToken, userDetails.getAccessToken());
        if (!equalsAccessToken) throw new TokenAuthenticationException(StrUtil.format("Token 已弃用 [{}]", username));

        return userDetails;
      })
      .orElseThrow(() -> new TokenAuthenticationException(StrUtil.format("Token 已过期 [{}]", username)));
  }

  private void checkForUserIsEnabled(User user) {
    boolean isDisabled = user.getEnabled() != YesOrNo.YES;
    if (isDisabled) throw new TokenAuthenticationException(StrUtil.format("用户已禁用 [{}]", user.getUsername()));
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

  private void checkForRefreshTokenExists(String username, String refreshToken) {
    Optional.ofNullable(tokenCache.getIfPresent(username))
      .ifPresentOrElse(
        u -> {
          boolean equalsRefreshToken = StrUtil.equals(refreshToken, u.getRefreshToken());
          if (!equalsRefreshToken) throw new TokenAuthenticationException(StrUtil.format("Token 已弃用 [{}]", username));
        },
        () -> {
          throw new TokenAuthenticationException(StrUtil.format("Token 已过期 [{}]", username));
        }
      );
  }
}
