package net.wuxianjie.web.user;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.handler.YesOrNo;
import net.wuxianjie.springbootcore.security.SecurityConfigData;
import net.wuxianjie.springbootcore.security.TokenData;
import net.wuxianjie.springbootcore.security.TokenService;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.JwtUtils;
import net.wuxianjie.springbootcore.shared.NotFoundException;
import net.wuxianjie.springbootcore.shared.TokenAuthenticationException;
import net.wuxianjie.web.shared.BeanQualifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenServiceImpl implements TokenService {

  @Qualifier(BeanQualifiers.TOKEN_CACHE)
  private final Cache<String, TokenUserDetails> tokenCache;

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final SecurityConfigData securityConfig;

  @Override
  public TokenData getToken(String accountName, String accountRawPassword) {
    final User user = getUserFromDbMustBeExists(accountName);

    validateAccountAvailable(user.getEnabled(), user.getUsername());

    validatePassword(accountRawPassword, user.getHashedPassword());

    final TokenData token = createNewToken(user);

    addToCache(user, token);

    return token;
  }

  @Override
  public TokenData refreshToken(String refreshToken) {
    final Map<String, Object> payload = JwtUtils.verifyTwtReturnPayload(
        securityConfig.getJwtSigningKey(), refreshToken);

    final String username = (String) payload.get(TokenAttributes.ACCOUNT_KEY);
    final String tokenType =
        (String) payload.get(TokenAttributes.TOKEN_TYPE_KEY);

    if (!Objects.equals(tokenType, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE)) {
      throw new TokenAuthenticationException("Token 类型错误");
    }

    final User user = getUserFromDbMustBeExists(username);

    validateAccountAvailable(user.getEnabled(), username);

    final TokenData token = createNewToken(user);

    addToCache(user, token);

    return token;
  }

  private User getUserFromDbMustBeExists(String username) {
    final Optional<User> userOptional = userService.getUser(username);

    if (userOptional.isEmpty()) {
      throw new NotFoundException(String.format("账号【%s】不存在", username));
    }

    return userOptional.get();
  }

  private void validateAccountAvailable(YesOrNo enabled, String username) {
    if (enabled != YesOrNo.YES) {
      throw new TokenAuthenticationException(String.format("账号【%s】已被禁用",
          username));
    }
  }

  private void validatePassword(String rawPassword, String hashedPassword) {
    final boolean isPasswordCorrect =
        passwordEncoder.matches(rawPassword, hashedPassword);

    if (!isPasswordCorrect) {
      throw new TokenAuthenticationException("密码错误");
    }
  }

  private TokenData createNewToken(User user) {
    final Map<String, Object> jwtPayload = new HashMap<>();

    jwtPayload.put(TokenAttributes.ACCOUNT_KEY, user.getUsername());
    jwtPayload.put(TokenAttributes.ROLE_KEY, user.getRoles());

    final String accessToken =
        createNewToken(jwtPayload, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);

    final String refreshToken =
        createNewToken(jwtPayload, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);

    return new TokenData(TokenAttributes.EXPIRES_IN_SECONDS_VALUE,
        accessToken, refreshToken);
  }

  private void addToCache(User user, TokenData token) {
    final TokenUserDetails userDetails = new TokenUserDetails(
        user.getUserId(), user.getUsername(), user.getRoles(),
        token.getAccessToken(), token.getRefreshToken());

    tokenCache.put(user.getUsername(), userDetails);
  }

  private String createNewToken(Map<String, Object> jwtPayload,
                                String tokenType) {
    jwtPayload.put(TokenAttributes.TOKEN_TYPE_KEY, tokenType);

    return JwtUtils.createNewJwt(securityConfig.getJwtSigningKey(),
        jwtPayload, TokenAttributes.EXPIRES_IN_SECONDS_VALUE);
  }
}
