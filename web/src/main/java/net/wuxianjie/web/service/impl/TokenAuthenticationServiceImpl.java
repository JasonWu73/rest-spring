package net.wuxianjie.web.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.constant.BeanQualifiers;
import net.wuxianjie.core.dto.PrincipalDto;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.service.TokenAuthenticationService;
import net.wuxianjie.core.util.JwtUtils;
import net.wuxianjie.web.constant.TokenAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 实现 Token 认证机制
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

    @Qualifier(BeanQualifiers.JWT_SIGNING_KEY)
    private final String jwtSigningKey;

    private final Cache<String, PrincipalDto> tokenCache;

    @Override
    public PrincipalDto authenticate(@NonNull final String accessToken) {
        // 解析 Token
        final Map<String, Object> payload = JwtUtils.verifyAndParseToken(jwtSigningKey, accessToken);
        final String username = (String) payload.get(TokenAttributes.ACCOUNT_KEY);

        // 获取缓存中的 Token
        final PrincipalDto principal = tokenCache.getIfPresent(username);

        // 校验 Access Token 是否有效
        if (principal == null || !principal.getAccessToken().equals(accessToken)) {
            throw new TokenAuthenticationException("Token 已过期");
        }

        return principal;
    }
}
