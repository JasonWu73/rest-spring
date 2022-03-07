package net.wuxianjie.web.auth;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.rest.auth.TokenAuthenticationService;
import net.wuxianjie.core.rest.auth.dto.AuthConfigDto;
import net.wuxianjie.core.rest.auth.dto.PrincipalDto;
import net.wuxianjie.core.shared.exception.TokenAuthenticationException;
import net.wuxianjie.core.shared.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 实现 Token 认证机制
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationServiceImpl implements
        TokenAuthenticationService {

    private final AuthConfigDto authConfig;
    private final Cache<String, PrincipalDto> tokenCache;

    @Override
    public PrincipalDto authenticate(@NonNull final String accessToken) {
        // 解析 Token
        final Map<String, Object> payload = JwtUtils.verifyAndParseToken(
                authConfig.getJwtSigningKey(),
                accessToken
        );
        final String username =
                (String) payload.get(TokenAttributes.ACCOUNT_KEY);

        // 获取缓存中的 Token
        final PrincipalDto principal = tokenCache.getIfPresent(username);

        // 校验 Access Token 是否有效
        if (principal == null ||
                !principal.getAccessToken().equals(accessToken)
        ) {
            throw new TokenAuthenticationException("Token 已过期");
        }

        return principal;
    }
}
