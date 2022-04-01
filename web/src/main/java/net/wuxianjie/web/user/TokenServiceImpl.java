package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.SecurityConfigData;
import net.wuxianjie.springbootcore.security.TokenData;
import net.wuxianjie.springbootcore.security.TokenService;
import net.wuxianjie.springbootcore.shared.exception.NotFoundException;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.shared.util.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 实现 Access Token 管理业务逻辑。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityConfigData securityConfig;
    private final Cache<String, UserDetails> tokenCache;

    @Override
    public TokenData getToken(final String account,
                              final String password) throws TokenAuthenticationException {
        final User user = getUserFromDbMustBeExists(account);

        verifyAccountAvailable(user.getEnabled());

        verifyPassword(password, user.getHashedPassword());

        final TokenData token = generateToken(user);

        addToCache(user, token);

        return token;
    }

    @Override
    public TokenData refreshToken(final String refreshToken) throws TokenAuthenticationException {
        final Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), refreshToken);

        final String tokenType = Optional.ofNullable((String) payload.get(TokenAttributes.TOKEN_TYPE_KEY))
                .orElseThrow(() -> new TokenAuthenticationException(
                        StrUtil.format("Token 缺少 {} 信息", TokenAttributes.TOKEN_TYPE_KEY)));

        if (!StrUtil.equals(tokenType, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE)) {
            throw new TokenAuthenticationException("Token 类型错误");
        }

        final String username = Optional.ofNullable((String) payload.get(TokenAttributes.ACCOUNT_KEY))
                .orElseThrow(() -> new TokenAuthenticationException(
                        StrUtil.format("Token 缺少 {} 信息", TokenAttributes.ACCOUNT_KEY)));

        final User user = getUserFromDbMustBeExists(username);

        verifyAccountAvailable(user.getEnabled());

        final TokenData token = generateToken(user);

        addToCache(user, token);

        return token;
    }

    private User getUserFromDbMustBeExists(String username) {
        return Optional.ofNullable(userMapper.selectUserByName(username))
                .orElseThrow(() -> new NotFoundException("账号不存在"));
    }

    private void verifyAccountAvailable(final YesOrNo enabled) {
        if (enabled != YesOrNo.YES) throw new TokenAuthenticationException("账号已被禁用");
    }

    private void verifyPassword(final String rawPassword, final String hashedPassword) {
        final boolean isMatched = passwordEncoder.matches(rawPassword, hashedPassword);
        if (!isMatched) throw new TokenAuthenticationException("密码错误");
    }

    private TokenData generateToken(final User user) {
        final Map<String, Object> payload = new HashMap<>();
        payload.put(TokenAttributes.ACCOUNT_KEY, user.getUsername());
        payload.put(TokenAttributes.ROLE_KEY, user.getRoles());

        final String accessToken = generateToken(payload, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
        final String refreshToken = generateToken(payload, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);
        return new TokenData(TokenAttributes.EXPIRES_IN_SECONDS_VALUE, accessToken, refreshToken);
    }

    private String generateToken(final Map<String, Object> payload, final String tokenType) {
        payload.put(TokenAttributes.TOKEN_TYPE_KEY, tokenType);
        return JwtUtils.generateJwt(
                securityConfig.getJwtSigningKey(),
                payload,
                TokenAttributes.EXPIRES_IN_SECONDS_VALUE
        );
    }

    private void addToCache(final User user, final TokenData token) {
        final UserDetails userDetails = new UserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getRoles(),
                token.getAccessToken(),
                token.getRefreshToken()
        );
        tokenCache.put(user.getUsername(), userDetails);
    }
}
