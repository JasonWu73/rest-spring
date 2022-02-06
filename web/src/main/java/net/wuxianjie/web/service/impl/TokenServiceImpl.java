package net.wuxianjie.web.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.constant.BeanQualifiers;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.model.dto.CachedTokenDto;
import net.wuxianjie.core.model.dto.TokenDto;
import net.wuxianjie.core.service.TokenService;
import net.wuxianjie.core.util.JwtUtils;
import net.wuxianjie.web.constant.TokenAttributes;
import net.wuxianjie.web.model.entity.AccountEntity;
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

  /** 加密后的测试密码 ({@code 123}) */
  private static final String ENCODED_PASSWORD = "$2a$10$9Tq5H9wCOiRg97zR5K.6ye.5TIAQUVhDPFhm5YsbvSgpJhwTj3.yW";

  /** 模块数据库中数据 */
  private static final Map<String, AccountEntity> users = new HashMap<>() {{
    put("guest", new AccountEntity(1, "guest", ENCODED_PASSWORD, ""));
    put("user", new AccountEntity(2, "user", ENCODED_PASSWORD, "user"));
    put("admin", new AccountEntity(3, "admin", ENCODED_PASSWORD, "user,admin"));
  }};

  @Qualifier(BeanQualifiers.JWT_SIGNING_KEY) private final String jwtSigningKey;
  private final Cache<String, CachedTokenDto> tokenCache;
  private final PasswordEncoder passwordEncoder;

  @Override
  public TokenDto createToken(@NonNull final String accountName, @NonNull final String accountPassword) {
    // 根据账号名从数据库查询账号信息，并判断密码是否正确
    final AccountEntity userInDb = getAccount(accountName, accountPassword);

    if (userInDb == null) {
      throw new TokenAuthenticationException("账号名或密码错误");
    }

    // 构造写入缓存中的 Token 数据
    final CachedTokenDto tokenDto = new CachedTokenDto();
    tokenDto.setAccountId(userInDb.getId());
    tokenDto.setAccountName(userInDb.getName());
    tokenDto.setRoles(userInDb.getRoles());

    // 生成 Access Token 与 Refresh Token
    return generateToken(tokenDto);
  }

  @Override
  public TokenDto updateToken(@NonNull final String refreshToken) {
    // 解析 Token
    final Map<String, Object> payload = JwtUtils.verifyAndParseToken(jwtSigningKey, refreshToken);
    final String accountName = (String) payload.get(TokenAttributes.TOKEN_ACCOUNT);
    final String tokenType = (String) payload.get(TokenAttributes.TOKEN_TYPE);

    if (!tokenType.equals(TokenAttributes.REFRESH_TOKEN)) {
      throw new TokenAuthenticationException("Token 类型错误");
    }

    // 从缓存中查询 Refresh Token
    final CachedTokenDto cachedToken = tokenCache.getIfPresent(accountName);

    // 核验缓存中的 Refresh Token 与传入的 Refresh Token
    if (cachedToken == null || !cachedToken.getRefreshToken().equals(refreshToken)) {
      throw new TokenAuthenticationException("Token 已过期");
    }

    // 查询并更新程序内部的 Token 数据
    final AccountEntity account = users.get(accountName);
    cachedToken.setRoles(account.getRoles());

    // 生成 Access Token 与 Refresh Token
    return generateToken(cachedToken);
  }

  private AccountEntity getAccount(final String accountName, final String accountPassword) {

    final AccountEntity user = users.get(accountName);

    if (user == null || !passwordEncoder.matches(accountPassword, user.getPassword())) {
      return null;
    }

    return user;
  }

  private TokenDto generateToken(final CachedTokenDto tokenDto) {

    final Map<String, Object> jwtPayload = new HashMap<>();
    jwtPayload.put(TokenAttributes.TOKEN_ACCOUNT, tokenDto.getAccountName());

    final String accessToken = generateToken(jwtPayload, TokenAttributes.ACCESS_TOKEN);
    final String refreshToken = generateToken(jwtPayload, TokenAttributes.REFRESH_TOKEN);

    // 完善 Token 数据
    tokenDto.setAccessToken(accessToken);
    tokenDto.setRefreshToken(refreshToken);

    // 写入缓存
    tokenCache.put(tokenDto.getAccountName(), tokenDto);

    return new TokenDto(TokenAttributes.TOKEN_EXPIRES_IN_SECONDS, accessToken, refreshToken);
  }

  private String generateToken(final Map<String, Object> jwtPayload, final String tokenType) {

    jwtPayload.put(TokenAttributes.TOKEN_TYPE, tokenType);

    return JwtUtils.generateToken(jwtSigningKey, jwtPayload, TokenAttributes.TOKEN_EXPIRES_IN_SECONDS);
  }
}
