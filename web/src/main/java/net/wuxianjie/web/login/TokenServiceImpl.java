package net.wuxianjie.web.login;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.exception.NotFoundException;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.security.SecurityConfigData;
import net.wuxianjie.core.security.TokenData;
import net.wuxianjie.core.security.TokenService;
import net.wuxianjie.core.security.TokenUserDetails;
import net.wuxianjie.core.util.JwtUtils;
import net.wuxianjie.web.shared.BeanQualifiers;
import net.wuxianjie.web.user.ManagementOfUser;
import net.wuxianjie.web.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        ManagementOfUser user = getUserFromDbMustBeExists(accountName);

        checkPassword(accountRawPassword, user.getHashedPassword());

        TokenData token = createNewToken(user);

        addToCache(user, token);

        return token;
    }

    @Override
    public TokenData refreshToken(String refreshToken) {
        Map<String, Object> payload = JwtUtils.verifyTwtReturnPayload(securityConfig.getJwtSigningKey(), refreshToken);
        String username = (String) payload.get(TokenAttributes.ACCOUNT_KEY);
        String tokenType = (String) payload.get(TokenAttributes.TOKEN_TYPE_KEY);

        if (!TokenAttributes.REFRESH_TOKEN_TYPE_VALUE.equals(tokenType)) {
            throw new TokenAuthenticationException("Token 类型错误");
        }

        ManagementOfUser user = getUserFromDbMustBeExists(username);

        TokenData token = createNewToken(user);

        addToCache(user, token);

        return token;
    }

    private ManagementOfUser getUserFromDbMustBeExists(String username) {
        ManagementOfUser user = userService.getUser(username);

        if (user == null) {
            throw new NotFoundException(String.format("账号【%s】不存在", username));
        }

        return user;
    }

    private void checkPassword(String rawPassword, String hashedPassword) {
        boolean isPasswordCorrect = passwordEncoder.matches(rawPassword, hashedPassword);

        if (!isPasswordCorrect) {
            throw new BadRequestException("密码错误");
        }
    }

    private TokenData createNewToken(ManagementOfUser user) {
        Map<String, Object> jwtPayload = new HashMap<>();
        jwtPayload.put(TokenAttributes.ACCOUNT_KEY, user.getUsername());
        jwtPayload.put(TokenAttributes.ROLE_KEY, user.getRoles());

        String accessToken = createNewToken(jwtPayload, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
        String refreshToken = createNewToken(jwtPayload, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);

        return new TokenData(TokenAttributes.EXPIRES_IN_SECONDS_VALUE, accessToken, refreshToken);
    }

    private void addToCache(ManagementOfUser user, TokenData token) {
        TokenUserDetails userDetails = new TokenUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getRoles(),
                token.getAccessToken(),
                token.getRefreshToken()
        );

        tokenCache.put(user.getUsername(), userDetails);
    }

    private String createNewToken(Map<String, Object> jwtPayload, String tokenType) {
        jwtPayload.put(TokenAttributes.TOKEN_TYPE_KEY, tokenType);

        return JwtUtils.createNewJwt(
                securityConfig.getJwtSigningKey(),
                jwtPayload,
                TokenAttributes.EXPIRES_IN_SECONDS_VALUE
        );
    }
}
