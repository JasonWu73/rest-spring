package net.wuxianjie.web.user;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.security.SecurityConfigData;
import net.wuxianjie.core.security.TokenData;
import net.wuxianjie.core.security.TokenService;
import net.wuxianjie.core.security.TokenUserDetails;
import net.wuxianjie.core.shared.BadRequestException;
import net.wuxianjie.core.shared.JwtUtils;
import net.wuxianjie.core.shared.NotFoundException;
import net.wuxianjie.core.shared.TokenAuthenticationException;
import net.wuxianjie.web.shared.BeanQualifiers;
import net.wuxianjie.web.shared.YesOrNo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
        final ManagementOfUser user = getUserFromDbMustBeExists(accountName);

        checkAccountIsDisabled(user.getEnabled(), user.getUsername());

        checkPassword(accountRawPassword, user.getHashedPassword());

        final TokenData token = createNewToken(user);

        addToCache(user, token);

        return token;
    }

    @Override
    public TokenData refreshToken(String refreshToken) {
        final String signingKey = securityConfig.getJwtSigningKey();
        final Map<String, Object> payload = JwtUtils.verifyTwtReturnPayload(signingKey, refreshToken);
        final String username = (String) payload.get(TokenAttributes.ACCOUNT_KEY);
        final String tokenType = (String) payload.get(TokenAttributes.TOKEN_TYPE_KEY);

        if (!TokenAttributes.REFRESH_TOKEN_TYPE_VALUE.equals(tokenType)) {
            throw new TokenAuthenticationException("Token 类型错误");
        }

        final ManagementOfUser user = getUserFromDbMustBeExists(username);

        checkAccountIsDisabled(user.getEnabled(), username);

        final TokenData token = createNewToken(user);

        addToCache(user, token);

        return token;
    }

    @NonNull
    private ManagementOfUser getUserFromDbMustBeExists(String username) {
        final ManagementOfUser user = userService.getUser(username);

        if (user == null) {
            throw new NotFoundException(String.format("账号【%s】不存在", username));
        }

        return user;
    }

    private void checkAccountIsDisabled(Integer enabled, String username) {
        if (enabled == null || enabled != YesOrNo.YES.value()) {
            throw new TokenAuthenticationException(String.format("账号【%s】已被禁用", username));
        }
    }

    private void checkPassword(String rawPassword, String hashedPassword) {
        final boolean isPasswordCorrect = passwordEncoder.matches(rawPassword, hashedPassword);

        if (!isPasswordCorrect) {
            throw new BadRequestException("密码错误");
        }
    }

    @NonNull
    private TokenData createNewToken(ManagementOfUser user) {
        final Map<String, Object> jwtPayload = new HashMap<>();
        jwtPayload.put(TokenAttributes.ACCOUNT_KEY, user.getUsername());
        jwtPayload.put(TokenAttributes.ROLE_KEY, user.getRoles());

        final String accessToken = createNewToken(jwtPayload, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
        final String refreshToken = createNewToken(jwtPayload, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);

        return new TokenData(TokenAttributes.EXPIRES_IN_SECONDS_VALUE, accessToken, refreshToken);
    }

    private void addToCache(ManagementOfUser user, TokenData token) {
        final TokenUserDetails userDetails = new TokenUserDetails(user.getUserId(), user.getUsername(),
                user.getRoles(), token.getAccessToken(), token.getRefreshToken());

        tokenCache.put(user.getUsername(), userDetails);
    }

    @NonNull
    private String createNewToken(Map<String, Object> jwtPayload, String tokenType) {
        jwtPayload.put(TokenAttributes.TOKEN_TYPE_KEY, tokenType);

        return JwtUtils.createNewJwt(securityConfig.getJwtSigningKey(),
                jwtPayload, TokenAttributes.EXPIRES_IN_SECONDS_VALUE);
    }
}
