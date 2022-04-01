package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.wuxianjie.springbootcore.security.SecurityConfigData;
import net.wuxianjie.springbootcore.security.TokenAuthenticationService;
import net.wuxianjie.springbootcore.shared.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.shared.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static net.wuxianjie.web.user.TokenAttributes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author 吴仙杰
 */
class TokenAuthenticationServiceImplTest {

    private Cache<String, UserDetails> tokenCache;
    private SecurityConfigData securityConfig;
    private TokenAuthenticationService underTest;

    @BeforeEach
    void setUp() {
        tokenCache = Caffeine.newBuilder().build();
        securityConfig = new SecurityConfigData("kbeiTd5Q7rQr7ZLsrv0OhEwSBf5teTqlQWNV5Az+vQ0=", null);
        underTest = new TokenAuthenticationServiceImpl(securityConfig, tokenCache);
    }

    @Test
    @DisplayName("通过身份认证")
    void itShouldCheckWhenAllRight() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE_VALUE);
        }};
        final String token = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        final UserDetails user = new UserDetails();
        user.setAccessToken(token);
        tokenCache.put(username, user);

        // when
        final TokenUserDetails actual = underTest.authenticate(token);

        // then
        assertThat(actual).isEqualTo(tokenCache.getIfPresent(username));
    }

    @Test
    @DisplayName("缓存中 Token 已刷新")
    void itShouldCheckWhenCacheTokenRefreshed() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE_VALUE);
        }};
        final String token = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        payload.put("a", "b"); // 使两次生成不同的 Token
        final String refreshedToken = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        final UserDetails userDetails = new UserDetails();
        userDetails.setAccessToken(refreshedToken);
        tokenCache.put(username, userDetails);

        // when
        assertThat(StrUtil.equals(token, refreshedToken)).isFalse();

        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("Token 已过期");
    }

    @Test
    @DisplayName("缓存不存在")
    void itShouldCheckWhenCacheNotExists() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE_VALUE);
        }};
        final String token = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        // when
        assertThat(tokenCache.getIfPresent(username)).isNull();

        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("Token 已过期");
    }

    @Test
    @DisplayName("Token 类型错误")
    void itShouldCheckWhenProvideRefreshToken() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, REFRESH_TOKEN_TYPE_VALUE);
        }};
        final String token = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        final UserDetails userDetails = new UserDetails();
        userDetails.setAccessToken(token);
        tokenCache.put(username, userDetails);

        // when
        assertThat(payload.get(TOKEN_TYPE_KEY)).isNotEqualTo(ACCESS_TOKEN_TYPE_VALUE);

        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("Token 类型错误");
    }

    @Test
    @DisplayName("Token 缺少类型")
    void itShouldCheckWhenTokenLackOfType() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
        }};
        final String token = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        final UserDetails userDetails = new UserDetails();
        userDetails.setAccessToken(token);
        tokenCache.put(username, userDetails);

        // when
        assertThat(payload.get(TOKEN_TYPE_KEY)).isNull();

        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage(StrUtil.format("Token 缺少 {} 信息", TOKEN_TYPE_KEY));
    }

    @Test
    @DisplayName("Token 缺少账号")
    void itShouldCheckWhenTokenLackOfAccount() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE_VALUE);
        }};
        final String token = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        final UserDetails userDetails = new UserDetails();
        userDetails.setAccessToken(token);
        tokenCache.put(username, userDetails);

        // when
        assertThat(payload.get(ACCOUNT_KEY)).isNull();

        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage(StrUtil.format("Token 缺少 {} 信息", ACCOUNT_KEY));
    }
}