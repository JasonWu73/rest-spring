package net.wuxianjie.web.login;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.security.SecurityConfigData;
import net.wuxianjie.core.security.TokenAuthenticationService;
import net.wuxianjie.core.security.TokenUserDetails;
import net.wuxianjie.core.util.JwtUtils;
import net.wuxianjie.web.shared.BeanQualifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

    @Qualifier(BeanQualifiers.TOKEN_CACHE)
    private final Cache<String, TokenUserDetails> tokenCache;

    private final SecurityConfigData securityConfig;

    @Override
    public TokenUserDetails authenticate(String accessToken) {
        String username = getUsernameFromAccessToken(accessToken);

        return getUserDetailsFromCache(username, accessToken);
    }

    private String getUsernameFromAccessToken(String accessToken) {
        Map<String, Object> payload = JwtUtils.verifyTwtReturnPayload(securityConfig.getJwtSigningKey(), accessToken);

        return (String) payload.get(TokenAttributes.ACCOUNT_KEY);
    }

    private TokenUserDetails getUserDetailsFromCache(String username, String accessToken) {
        TokenUserDetails userDetails = tokenCache.getIfPresent(username);

        if (userDetails == null || !accessToken.equals(userDetails.getAccessToken())) {
            throw new TokenAuthenticationException("Token 已过期");
        }

        return userDetails;
    }
}
