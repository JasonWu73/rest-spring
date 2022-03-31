package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.wuxianjie.springbootcore.security.SecurityConfigData;
import net.wuxianjie.springbootcore.security.TokenAuthenticationService;
import net.wuxianjie.springbootcore.security.UserDetails;
import net.wuxianjie.springbootcore.shared.JwtUtils;
import net.wuxianjie.springbootcore.shared.TokenAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author 吴仙杰
 */
class TokenAuthenticationServiceImplTest {

    private Cache<String, TokenUserDetails> tokenCache;
    private SecurityConfigData securityConfig;
    private TokenAuthenticationService underTest;

    @BeforeEach
    void setUp() {
        tokenCache = Caffeine.newBuilder().build();
        securityConfig = new SecurityConfigData("kbeiTd5Q7rQr7ZLsrv0OhEwSBf5teTqlQWNV5Az+vQ0=", null);
        underTest = new TokenAuthenticationServiceImpl(tokenCache, securityConfig);
    }

    @Test
    @DisplayName("通过身份认证")
    void itShouldCheckWhenAllRight() {
        // given
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {{
            put(TokenAttributes.ACCOUNT_KEY, username);
            put(TokenAttributes.TOKEN_TYPE_KEY, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
        }};
        String token = JwtUtils.createJwt(securityConfig.getJwtSigningKey(), payload, 60);

        TokenUserDetails user = new TokenUserDetails();
        user.setAccessToken(token);
        tokenCache.put(username, user);

        // when
        UserDetails actual = underTest.authenticate(token);

        // then
        assertThat(actual).isEqualTo(tokenCache.getIfPresent(username));
    }

    @Test
    @DisplayName("缓存中 Token 已刷新")
    void itShouldCheckWhenCacheTokenRefreshed() throws InterruptedException {
        // given
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {{
            put(TokenAttributes.ACCOUNT_KEY, username);
            put(TokenAttributes.TOKEN_TYPE_KEY, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
        }};
        String token = JwtUtils.createJwt(securityConfig.getJwtSigningKey(), payload, 60);

        Thread.sleep(100); // 保障两次生成不同的 Token
        String refreshedToken = JwtUtils.createJwt(securityConfig.getJwtSigningKey(), payload, 60);

        TokenUserDetails userDetails = new TokenUserDetails();
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
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {{
            put(TokenAttributes.ACCOUNT_KEY, username);
            put(TokenAttributes.TOKEN_TYPE_KEY, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
        }};
        String token = JwtUtils.createJwt(securityConfig.getJwtSigningKey(), payload, 60);

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
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {{
            put(TokenAttributes.ACCOUNT_KEY, username);
            put(TokenAttributes.TOKEN_TYPE_KEY, TokenAttributes.REFRESH_TOKEN_TYPE_VALUE);
        }};
        String token = JwtUtils.createJwt(securityConfig.getJwtSigningKey(), payload, 60);

        TokenUserDetails userDetails = new TokenUserDetails();
        userDetails.setAccessToken(token);
        tokenCache.put(username, userDetails);

        // when
        assertThat(payload.get(TokenAttributes.TOKEN_TYPE_KEY))
                .isNotEqualTo(TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);

        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("Token 类型错误");
    }

    @Test
    @DisplayName("Token 缺少类型")
    void itShouldCheckWhenTokenLackOfType() {
        // given
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {{
            put(TokenAttributes.ACCOUNT_KEY, username);
        }};
        String token = JwtUtils.createJwt(securityConfig.getJwtSigningKey(), payload, 60);

        TokenUserDetails userDetails = new TokenUserDetails();
        userDetails.setAccessToken(token);
        tokenCache.put(username, userDetails);

        // when
        assertThat(payload.get(TokenAttributes.TOKEN_TYPE_KEY)).isNull();

        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage(StrUtil.format("Token 缺少 {} 信息", TokenAttributes.TOKEN_TYPE_KEY));
    }

    @Test
    @DisplayName("Token 缺少账号")
    void itShouldCheckWhenTokenLackOfAccount() {
        // given
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {{
            put(TokenAttributes.TOKEN_TYPE_KEY, TokenAttributes.ACCESS_TOKEN_TYPE_VALUE);
        }};
        String token = JwtUtils.createJwt(securityConfig.getJwtSigningKey(), payload, 60);

        TokenUserDetails userDetails = new TokenUserDetails();
        userDetails.setAccessToken(token);
        tokenCache.put(username, userDetails);

        // when
        assertThat(payload.get(TokenAttributes.ACCOUNT_KEY)).isNull();

        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage(StrUtil.format("Token 缺少 {} 信息", TokenAttributes.ACCOUNT_KEY));
    }
}