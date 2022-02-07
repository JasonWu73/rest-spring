package net.wuxianjie.web.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.constant.BeanQualifiers;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.domain.CachedToken;
import net.wuxianjie.core.domain.Token;
import net.wuxianjie.core.service.TokenService;
import net.wuxianjie.core.util.JwtUtils;
import net.wuxianjie.web.constant.TokenAttributes;
import net.wuxianjie.web.domain.Account;
import net.wuxianjie.web.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于本地缓存实现 Token 生成与刷新
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
  public Token createToken(@NonNull final String accountName, @NonNull final String accountPassword) {
    // 根据账号名从数据库查询账号信息
    final Account account = loadAccount(accountName);

    if (account == null) {
      throw new TokenAuthenticationException("账号名或密码错误");
    }

    // 判断密码是否正确
    final boolean rightedPassword = isRightPassword(accountPassword, account.getAccountPassword());

    if (!rightedPassword) {
      throw new TokenAuthenticationException("账号名或密码错误");
    }

    // 构造写入缓存中的 Token 数据
    final CachedToken cachedToken = new CachedToken();
    cachedToken.setAccountId(account.getAccountId());
    cachedToken.setAccountName(account.getAccountName());
    cachedToken.setRoles(account.getAccountRoles());

    // 生成 Access Token 与 Refresh Token
    return generateToken(cachedToken);
  }

  @Override
  public Token updateToken(@NonNull final String refreshToken) {
    // 解析 Token
    final Map<String, Object> payload = JwtUtils.verifyAndParseToken(jwtSigningKey, refreshToken);
    final String accountName = (String) payload.get(TokenAttributes.TOKEN_ACCOUNT);
    final String tokenType = (String) payload.get(TokenAttributes.TOKEN_TYPE);

    if (!tokenType.equals(TokenAttributes.REFRESH_TOKEN)) {
      throw new TokenAuthenticationException("Token 类型错误");
    }

    // 从缓存中查询 Refresh Token
    final CachedToken cachedToken = tokenCache.getIfPresent(accountName);

    // 核验缓存中的 Refresh Token 与传入的 Refresh Token
    if (cachedToken == null || !cachedToken.getRefreshToken().equals(refreshToken)) {
      throw new TokenAuthenticationException("Token 已过期");
    }

    // 查询并更新程序内部的 Token 数据
    final Account account = loadAccount(accountName);
    cachedToken.setRoles(account.getAccountRoles());

    // 生成 Access Token 与 Refresh Token
    return generateToken(cachedToken);
  }

  private Account loadAccount(final String accountName) {
    return accountMapper.queryAccountByUserName(accountName);
  }

  private boolean isRightPassword(final String rawPassword, final String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  private Token generateToken(final CachedToken tokenDto) {

    final Map<String, Object> jwtPayload = new HashMap<>();
    jwtPayload.put(TokenAttributes.TOKEN_ACCOUNT, tokenDto.getAccountName());

    final String accessToken = generateToken(jwtPayload, TokenAttributes.ACCESS_TOKEN);
    final String refreshToken = generateToken(jwtPayload, TokenAttributes.REFRESH_TOKEN);

    // 完善 Token 数据
    tokenDto.setAccessToken(accessToken);
    tokenDto.setRefreshToken(refreshToken);

    // 写入缓存
    tokenCache.put(tokenDto.getAccountName(), tokenDto);

    return new Token(TokenAttributes.TOKEN_EXPIRES_IN_SECONDS, accessToken, refreshToken);
  }

  private String generateToken(final Map<String, Object> jwtPayload, final String tokenType) {

    jwtPayload.put(TokenAttributes.TOKEN_TYPE, tokenType);

    return JwtUtils.generateToken(jwtSigningKey, jwtPayload, TokenAttributes.TOKEN_EXPIRES_IN_SECONDS);
  }
}
