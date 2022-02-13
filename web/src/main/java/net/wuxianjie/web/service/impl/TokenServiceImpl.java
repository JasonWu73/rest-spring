package net.wuxianjie.web.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.constant.BeanQualifiers;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.model.CachedToken;
import net.wuxianjie.core.model.Token;
import net.wuxianjie.core.service.TokenService;
import net.wuxianjie.core.util.JwtUtils;
import net.wuxianjie.web.constant.TokenAttributes;
import net.wuxianjie.web.model.Account;
import net.wuxianjie.web.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于本地缓存实现Token生成与刷新
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenServiceImpl implements TokenService {

  @Qualifier(BeanQualifiers.JWT_SIGNING_KEY) private final String jwtSigningKey;
  private final Cache<String, CachedToken> tokenCache;
  private final PasswordEncoder passwordEncoder;
  private final AccountMapper accountMapper;

  @Override
  public Token getToken(@NonNull final String accountName, @NonNull final String accountPassword) {
    // 根据账号名从数据库查询账号信息
    final Account account = getAccount(accountName);

    if (account == null) {
      throw new TokenAuthenticationException("账号名或密码错误");
    }

    // 判断密码是否正确
    final boolean rightedPassword = isRightPassword(accountPassword, account.getPassword());

    if (!rightedPassword) {
      throw new TokenAuthenticationException("账号名或密码错误");
    }

    // 构造写入缓存中的Token数据
    final CachedToken cachedToken = new CachedToken();
    cachedToken.setAccountId(account.getAccountId());
    cachedToken.setAccountName(account.getAccountName());
    cachedToken.setRoles(account.getRoles());

    // 生成Access Token与Refresh Token
    return generateToken(cachedToken);
  }

  @Override
  public Token updateToken(@NonNull final String refreshToken) {
    // 解析Token
    final Map<String, Object> payload = JwtUtils.verifyAndParseToken(jwtSigningKey, refreshToken);
    final String accountName = (String) payload.get(TokenAttributes.TOKEN_ACCOUNT);
    final String tokenType = (String) payload.get(TokenAttributes.TOKEN_TYPE);

    if (!tokenType.equals(TokenAttributes.REFRESH_TOKEN)) {
      throw new TokenAuthenticationException("Token类型错误");
    }

    // 从缓存中查询Refresh Token
    final CachedToken cachedToken = tokenCache.getIfPresent(accountName);

    // 核验缓存中的Refresh Token与传入的Refresh Token
    if (cachedToken == null || !cachedToken.getRefreshToken().equals(refreshToken)) {
      throw new TokenAuthenticationException("Token已过期");
    }

    // 查询并更新程序内部的Token数据
    final Account account = getAccount(accountName);
    cachedToken.setRoles(account.getRoles());

    // 生成Access Token与Refresh Token
    return generateToken(cachedToken);
  }

  private Account getAccount(final String accountName) {
    return accountMapper.getAccount(accountName);
  }

  private boolean isRightPassword(final String rawPassword, final String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  private Token generateToken(final CachedToken cachedToken) {

    final Map<String, Object> jwtPayload = new HashMap<>();
    jwtPayload.put(TokenAttributes.TOKEN_ACCOUNT, cachedToken.getAccountName());

    final String accessToken = generateToken(jwtPayload, TokenAttributes.ACCESS_TOKEN);
    final String refreshToken = generateToken(jwtPayload, TokenAttributes.REFRESH_TOKEN);

    // 完善Token数据
    cachedToken.setAccessToken(accessToken);
    cachedToken.setRefreshToken(refreshToken);

    // 写入缓存
    tokenCache.put(cachedToken.getAccountName(), cachedToken);

    return new Token(TokenAttributes.TOKEN_EXPIRES_IN_SECONDS, accessToken, refreshToken);
  }

  private String generateToken(final Map<String, Object> jwtPayload, final String tokenType) {

    jwtPayload.put(TokenAttributes.TOKEN_TYPE, tokenType);

    return JwtUtils.generateToken(jwtSigningKey, jwtPayload, TokenAttributes.TOKEN_EXPIRES_IN_SECONDS);
  }
}
