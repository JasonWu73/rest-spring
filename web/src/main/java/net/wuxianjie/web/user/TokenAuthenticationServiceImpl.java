package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.shared.CoreConfigData;
import net.wuxianjie.springbootcore.security.TokenAuthenticationService;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.JwtUtils;
import net.wuxianjie.springbootcore.shared.TokenAuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * 实现 Token 认证。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class TokenAuthenticationServiceImpl
        implements TokenAuthenticationService {

    private final Cache<String, TokenUserDetails> tokenCache;
    private final CoreConfigData securityConfig;

    @Override
    public TokenUserDetails authenticate(String token)
            throws TokenAuthenticationException {
        Map<String, Object> payload =
                JwtUtils.validateJwt(securityConfig.getJwtSigningKey(), token);

        String tokenType = Optional.ofNullable(
                        (String) payload.get(TokenAttributes.TOKEN_TYPE_KEY)
                )
                .orElseThrow(() ->
                        new TokenAuthenticationException(
                                StrUtil.format("Token 缺少 {} 信息",
                                        TokenAttributes.TOKEN_TYPE_KEY)
                        )
                );

        if (!StrUtil.equals(tokenType,
                TokenAttributes.ACCESS_TOKEN_TYPE_VALUE)
        ) {
            throw new TokenAuthenticationException("Token 类型错误");
        }

        String username = Optional.ofNullable(
                        (String) payload.get(TokenAttributes.ACCOUNT_KEY)
                )
                .orElseThrow(() ->
                        new TokenAuthenticationException(
                                StrUtil.format("Token 缺少 {} 信息",
                                        TokenAttributes.ACCOUNT_KEY)
                        )
                );

        return getUserDetailsFromCache(username, token);
    }

    private TokenUserDetails getUserDetailsFromCache(String username,
                                                     String token
    ) {
        TokenUserDetails userDetails = tokenCache.getIfPresent(username);

        if (userDetails == null
                || !StrUtil.equals(token, userDetails.getAccessToken())) {
            throw new TokenAuthenticationException("Token 已过期");
        }

        return userDetails;
    }
}
