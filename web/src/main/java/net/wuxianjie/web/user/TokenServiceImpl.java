package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.SecurityConfig;
import net.wuxianjie.springbootcore.security.TokenData;
import net.wuxianjie.springbootcore.security.TokenService;
import net.wuxianjie.springbootcore.util.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Access Token 管理业务逻辑实现类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  private final UserMapper userMapper;
  private final SecurityConfig securityConfig;
  private final PasswordEncoder passwordEncoder;
  private final Cache<String, UserDetails> tokenCache;

  @Override
  public TokenData getToken(String account, String password) throws TokenAuthenticationException {
    User user = getUserFromDbMustBeExists(account);

    verifyAccountUsability(user.getUsername(), user.getEnabled());

    boolean isMatched = passwordEncoder.matches(password, user.getHashedPassword());

    if (!isMatched) {
      throw new TokenAuthenticationException("密码错误");
    }

    TokenData token = generateToken(user);

    addToCache(user, token);

    return token;
  }

  @Override
  public TokenData refreshToken(String refreshToken) throws TokenAuthenticationException {
    Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), refreshToken);

    String tokenType = TokenUtils.getTokenType(payload);

    if (!StrUtil.equals(tokenType, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE)) {
      throw new TokenAuthenticationException("仅 Refresh Token 才可刷新");
    }

    String username = TokenUtils.getTokenAccount(payload);

    verifyRefreshTokenUsability(username, refreshToken);

    User user = getUserFromDbMustBeExists(username);

    verifyAccountUsability(user.getUsername(), user.getEnabled());

    TokenData token = generateToken(user);

    addToCache(user, token);

    return token;
  }

  private User getUserFromDbMustBeExists(String username) {
    return Optional.ofNullable(userMapper.findByUsername(username))
      .orElseThrow(() -> new NotFoundException("用户名 [" + username + "] 不存在"));
  }

  private void verifyAccountUsability(String username, YesOrNo enabled) {
    if (enabled != YesOrNo.YES) {
      throw new TokenAuthenticationException("账号 [" + username + "] 已被停用");
    }
  }

  private TokenData generateToken(User user) {
    Map<String, Object> payload = new HashMap<>();
    payload.put(TokenAttributes.ACCOUNT_KEY, user.getUsername());
    payload.put(TokenAttributes.ROLE_KEY, user.getRoles());

    String accessToken = generateToken(payload, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
    String refreshToken = generateToken(payload, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);

    return new TokenData(
      TokenAttributes.EXPIRES_IN_SECONDS_VALUE,
      accessToken,
      refreshToken
    );
  }

  private String generateToken(Map<String, Object> payload, String tokenType) {
    payload.put(TokenAttributes.TOKEN_TYPE_KEY, tokenType);

    return JwtUtils.generateJwt(
      securityConfig.getJwtSigningKey(),
      payload,
      TokenAttributes.EXPIRES_IN_SECONDS_VALUE
    );
  }

  private void addToCache(User user, TokenData token) {
    UserDetails userDetails = new UserDetails(
      user.getUserId(),
      user.getUsername(),
      user.getRoles(),
      token.getAccessToken(),
      token.getRefreshToken()
    );

    tokenCache.put(user.getUsername(), userDetails);
  }

  private void verifyRefreshTokenUsability(String username, String refreshToken) {
    Optional.ofNullable(tokenCache.getIfPresent(username))
      .ifPresentOrElse(
        u -> {
          if (!StrUtil.equals(refreshToken, u.getRefreshToken())) {
            throw new TokenAuthenticationException("Token 已弃用");
          }
        },
        () -> {
          throw new TokenAuthenticationException("Token 已过期");
        }
      );
  }
}
