package net.wuxianjie.springbootcore.security;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import net.wuxianjie.springbootcore.shared.JwtUtils;
import net.wuxianjie.springbootcore.shared.TokenAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static net.wuxianjie.springbootcore.security.TokenAuthenticationServiceImpl.*;
import static org.assertj.core.api.Assertions.*;

/**
 * @author 吴仙杰
 */
@ExtendWith(MockitoExtension.class)
class TokenAuthenticationServiceTest {

    private FIFOCache<String, TokenUserDetails> cache;
    private TokenAuthenticationService underTest;

    @BeforeEach
    void setUp() {
        cache = CacheUtil.newFIFOCache(1);
        underTest = new TokenAuthenticationServiceImpl(cache);
    }

    @Test
    @DisplayName("通过身份认证")
    void itShouldCheckWhenAllRight() {
        // given
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE_VALUE);
        }};
        String token = JwtUtils.createJwt(SIGNING_KEY, payload, 60);
        TokenUserDetails user = new TokenUserDetails();

        user.setAccessToken(token);

        cache.put(username, user);

        // when
        UserDetails actual = underTest.authenticate(token);

        // then
        assertThat(actual).isEqualTo(cache.get(username));
    }

    @Test
    @DisplayName("Token 缺少 account 字段")
    void willThrowExceptionWhenTokenNotContainAccountField() {
        // given
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {
            {
                put(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE_VALUE);
            }
        };
        String token = JwtUtils.createJwt(SIGNING_KEY, payload, 60);
        TokenUserDetails user = new TokenUserDetails();

        user.setAccessToken(token);

        cache.put(username, user);

        // when
        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessageContaining("Token 缺少 account 信息");
    }

    @Test
    @DisplayName("Token 缺少 type 字段")
    void willThrowExceptionWhenTokenNotContainTypeField() {
        // given
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {
            {
                put(ACCOUNT_KEY, username);
            }
        };
        String token = JwtUtils.createJwt(SIGNING_KEY, payload, 60);
        TokenUserDetails user = new TokenUserDetails();

        user.setAccessToken(token);

        cache.put(username, user);

        // when
        // then
        assertThatExceptionOfType(TokenAuthenticationException.class)
                .isThrownBy(() -> underTest.authenticate(token))
                .withMessageContaining("Token 缺少 type 信息");
    }

    @Test
    @DisplayName("Token 类型错误")
    void willThrowExceptionWhenTokenTypeError() {
        // given
        String username = "测试用户";
        Map<String, Object> payload = new HashMap<>() {
            {
                put(ACCOUNT_KEY, username);
                put(TOKEN_TYPE_KEY, "refresh");
            }
        };
        String token = JwtUtils.createJwt(SIGNING_KEY, payload, 60);
        TokenUserDetails user = new TokenUserDetails();

        user.setAccessToken(token);

        cache.put(username, user);

        // when
        // then
        assertThatThrownBy(() -> underTest.authenticate(token))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessageContaining("Token 类型错误");
    }
}