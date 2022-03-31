package net.wuxianjie.springbootcore.security;

import cn.hutool.cache.Cache;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.shared.util.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

    static final String SIGNING_KEY = "OpC34eGHy/qardZNkurHbcNohxTusA4nzQjyMTH2PAk=";
    static final String TOKEN_TYPE_KEY = "type";
    static final String ACCESS_TOKEN_TYPE_VALUE = "access";
    static final String REFRESH_TOKEN_TYPE_VALUE = "refresh";
    static final String ACCOUNT_KEY = "account";

    private final Cache<String, UserDetails> tokenCache;

    @Override
    public UserDetails authenticate(final String token) throws TokenAuthenticationException {
        final Map<String, Object> payload = JwtUtils.verifyJwt(SIGNING_KEY, token);

        final String tokenType = Optional.ofNullable((String) payload.get(TOKEN_TYPE_KEY))
                .orElseThrow(() -> new TokenAuthenticationException(
                        StrUtil.format("Token 缺少 {} 信息", TOKEN_TYPE_KEY)));

        if (!StrUtil.equals(tokenType, ACCESS_TOKEN_TYPE_VALUE)) {
            throw new TokenAuthenticationException("Token 类型错误");
        }

        final String username = Optional.ofNullable((String) payload.get(ACCOUNT_KEY))
                .orElseThrow(() -> new TokenAuthenticationException(
                        StrUtil.format("Token 缺少 {} 信息", ACCOUNT_KEY)));

        return getUserDetailsFromCache(username, token);
    }

    private UserDetails getUserDetailsFromCache(final String username, final String token) {
        final UserDetails userDetails = tokenCache.get(username);

        if (userDetails == null || !StrUtil.equals(token, userDetails.getAccessToken())) {
            throw new TokenAuthenticationException("Token 已过期");
        }

        return userDetails;
    }
}
