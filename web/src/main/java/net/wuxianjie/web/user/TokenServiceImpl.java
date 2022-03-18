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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Access Token 管理.
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Qualifier(BeanQualifiers.TOKEN_CACHE)
    private final Cache<String, TokenUserDetails> tokenCache;

    private final SecurityConfigData securityConfig;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public TokenData getToken(String accountName, String accountRawPassword) {
        User user = getUserFromDbMustBeExists(accountName);

        validateAccountAvailable(user.getEnabled(), user.getUsername());

        validatePassword(accountRawPassword, user.getHashedPassword());

        TokenData token = createNewToken(user);
        addToCache(user, token);

        return token;
    }

    @Override
    public TokenData refreshToken(String refreshToken) {
        Map<String, Object> payload = JwtUtils.validateJwtReturnPayload(securityConfig.getJwtSigningKey(), refreshToken);
        String tokenType = (String) payload.get(TokenAttributes.TOKEN_TYPE_KEY);
        if (!Objects.equals(tokenType, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE)) {
            throw new TokenAuthenticationException("Token 类型错误");
        }

        String username = (String) payload.get(TokenAttributes.ACCOUNT_KEY);
        User user = getUserFromDbMustBeExists(username);

        validateAccountAvailable(user.getEnabled(), username);

        TokenData token = createNewToken(user);
        addToCache(user, token);

        return token;
    }

    private void addToCache(User user, TokenData token) {
        TokenUserDetails userDetails = new TokenUserDetails(user.getUserId(), user.getUsername(), user.getRoles(),
                token.getAccessToken(), token.getRefreshToken());
        tokenCache.put(user.getUsername(), userDetails);
    }

    private TokenData createNewToken(User user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(TokenAttributes.ACCOUNT_KEY, user.getUsername());
        payload.put(TokenAttributes.ROLE_KEY, user.getRoles());

        String accessToken = createNewToken(payload, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
        String refreshToken = createNewToken(payload, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);
        return new TokenData(TokenAttributes.EXPIRES_IN_SECONDS_VALUE, accessToken, refreshToken);
    }

    private String createNewToken(Map<String, Object> payload, String tokenType) {
        payload.put(TokenAttributes.TOKEN_TYPE_KEY, tokenType);
        return JwtUtils.createNewJwt(securityConfig.getJwtSigningKey(), payload, TokenAttributes.EXPIRES_IN_SECONDS_VALUE);
    }

    private void validatePassword(String rawPassword, String hashedPassword) {
        boolean isMatched = passwordEncoder.matches(rawPassword, hashedPassword);
        if (!isMatched) {
            throw new TokenAuthenticationException("密码错误");
        }
    }

    private void validateAccountAvailable(YesOrNo enabled, String username) {
        if (enabled != YesOrNo.YES) {
            throw new TokenAuthenticationException(String.format("账号【%s】已被禁用", username));
        }
    }

    private User getUserFromDbMustBeExists(String username) {
        return userService.getUser(username)
                .orElseThrow(() -> new NotFoundException(String.format("账号【%s】不存在", username)));
    }
}
