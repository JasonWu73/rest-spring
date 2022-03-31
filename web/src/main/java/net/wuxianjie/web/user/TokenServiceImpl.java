package net.wuxianjie.web.user;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.SecurityConfigData;
import net.wuxianjie.springbootcore.security.TokenData;
import net.wuxianjie.springbootcore.security.TokenService;
import net.wuxianjie.springbootcore.shared.util.JwtUtils;
import net.wuxianjie.springbootcore.shared.exception.NotFoundException;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 实现 Access Token 管理业务逻辑。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final Cache<String, UserDetails> tokenCache;
    private final SecurityConfigData securityConfig;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public TokenData getToken(String account, String password)
            throws TokenAuthenticationException {
        User user = getUserFromDbMustBeExists(account);

        verifyAccountAvailable(user.getEnabled(), user.getUsername());

        VerifyPassword(password, user.getHashedPassword());

        TokenData token = createNewToken(user);
        addToCache(user, token);

        return token;
    }

    @Override
    public TokenData refreshToken(String refreshToken)
            throws TokenAuthenticationException {
        Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), refreshToken);
        String tokenType = (String) payload.get(TokenAttributes.TOKEN_TYPE_KEY);
        if (!Objects.equals(tokenType, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE)) {
            throw new TokenAuthenticationException("Token 类型错误");
        }

        String username = (String) payload.get(TokenAttributes.ACCOUNT_KEY);
        User user = getUserFromDbMustBeExists(username);

        verifyAccountAvailable(user.getEnabled(), username);

        TokenData token = createNewToken(user);
        addToCache(user, token);

        return token;
    }

    private void addToCache(User user, TokenData token) {
        UserDetails userDetails = new UserDetails(user.getUserId(), user.getUsername(), user.getRoles(),
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
        return JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, TokenAttributes.EXPIRES_IN_SECONDS_VALUE);
    }

    private void VerifyPassword(String rawPassword, String hashedPassword) {
        boolean isMatched = passwordEncoder.matches(rawPassword, hashedPassword);
        if (!isMatched) {
            throw new TokenAuthenticationException("密码错误");
        }
    }

    private void verifyAccountAvailable(YesOrNo enabled, String username) {
        if (enabled != YesOrNo.YES) {
            throw new TokenAuthenticationException(String.format("账号【%s】已被禁用", username));
        }
    }

    private User getUserFromDbMustBeExists(String username) {
        return userService.getUser(username)
                .orElseThrow(() -> new NotFoundException(String.format("账号【%s】不存在", username)));
    }
}
