package net.wuxianjie.web.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.SecurityConfigData;
import net.wuxianjie.springbootcore.security.TokenData;
import net.wuxianjie.springbootcore.shared.exception.NotFoundException;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.shared.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static net.wuxianjie.web.user.TokenAttributes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * @author 吴仙杰
 */
@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    private TokenServiceImpl underTest;

    @Mock
    private UserMapper userMapper;

    private Cache<String, UserDetails> tokenCache;
    private SecurityConfigData securityConfig;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        tokenCache = Caffeine.newBuilder().build();
        securityConfig = new SecurityConfigData("kbeiTd5Q7rQr7ZLsrv0OhEwSBf5teTqlQWNV5Az+vQ0=", null);
        underTest = new TokenServiceImpl(userMapper, passwordEncoder, tokenCache, securityConfig);
    }

    @Test
    @DisplayName("获取 Access Token")
    void canGetToken() {
        // given
        final String account = "fake_account";
        final String password = "123";

        final User user = new User();
        user.setUsername(account);
        user.setEnabled(YesOrNo.YES);
        user.setHashedPassword(passwordEncoder.encode(password));
        given(userMapper.selectUserByName(account)).willReturn(user);

        // when
        final TokenData token = underTest.getToken(account, password);

        // then
        assertThat(token.getAccessToken()).isNotNull();

        assertThat(tokenCache.getIfPresent(account)).isNotNull();
    }

    @Test
    @DisplayName("获取 Access Token 失败 - 找不到账号")
    void canNotGetTokenWhenNotFoundAccount() {
        // given
        final String account = "fake_account";
        final String password = "123";

        given(userMapper.selectUserByName(account)).willReturn(null);

        // when
        // then
        assertThatThrownBy(() -> underTest.getToken(account, password))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("未找到账号");

        assertThat(tokenCache.getIfPresent(account)).isNull();
    }

    @Test
    @DisplayName("获取 Access Token 失败 - 账号已禁用")
    void canNotGetTokenWhenAccountFrozen() {
        // given
        final String account = "fake_account";
        final String password = "123";

        final User user = new User();
        user.setUsername(account);
        user.setEnabled(YesOrNo.NO);
        user.setHashedPassword(passwordEncoder.encode("123"));
        given(userMapper.selectUserByName(account)).willReturn(user);

        // when
        // then
        assertThatThrownBy(() -> underTest.getToken(account, password))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("账号已禁用");

        assertThat(tokenCache.getIfPresent(account)).isNull();
    }

    @Test
    @DisplayName("获取 Access Token 失败 - 密码错误")
    void canNotGetTokenWhenPasswordWrong() {
        // given
        final String account = "fake_account";
        final String password = "123";

        final User user = new User();
        user.setUsername(account);
        user.setEnabled(YesOrNo.YES);
        user.setHashedPassword(passwordEncoder.encode("234"));
        given(userMapper.selectUserByName(account)).willReturn(user);

        // when
        // then
        assertThatThrownBy(() -> underTest.getToken(account, password))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("密码错误");

        assertThat(tokenCache.getIfPresent(account)).isNull();
    }

    @Test
    @DisplayName("刷新 Access Token")
    void canRefreshToken() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, REFRESH_TOKEN_TYPE_VALUE);
        }};
        final String refreshToken = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        final UserDetails userDetails = new UserDetails();
        userDetails.setRefreshToken(refreshToken);
        tokenCache.put(username, userDetails);

        final User user = new User();
        user.setUsername(username);
        user.setEnabled(YesOrNo.YES);
        given(userMapper.selectUserByName(username)).willReturn(user);

        // when
        final TokenData token = underTest.refreshToken(refreshToken);

        // then
        assertThat(token.getAccessToken()).isNotNull();

        assertThat(tokenCache.getIfPresent(username)).isNotNull();
    }

    @Test
    @DisplayName("刷新 Access Token 失败 - Token 无类型字段")
    void canNotRefreshTokenWhenTokenNoTypeField() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
        }};
        final String refreshToken = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("Token 缺少 type 信息");

        assertThat(tokenCache.getIfPresent(username)).isNull();
    }

    @Test
    @DisplayName("刷新 Access Token 失败 - Token 类型错误")
    void canNotRefreshTokenWhenTokenTypeWrong() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE_VALUE);
        }};
        final String refreshToken = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("Token 类型错误");

        assertThat(tokenCache.getIfPresent(username)).isNull();
    }

    @Test
    @DisplayName("刷新 Access Token 失败 - Token 无账号字段")
    void canNotRefreshTokenWhenTokenNoAccountField() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(TOKEN_TYPE_KEY, REFRESH_TOKEN_TYPE_VALUE);
        }};
        final String refreshToken = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("Token 缺少 account 信息");

        assertThat(tokenCache.getIfPresent(username)).isNull();
    }

    @Test
    @DisplayName("刷新 Access Token 失败 - Token 已过期")
    void canNotRefreshTokenWhenTokenExpired() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, REFRESH_TOKEN_TYPE_VALUE);
        }};
        final String refreshToken = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("Token 已过期");

        assertThat(tokenCache.getIfPresent(username)).isNull();
    }

    @Test
    @DisplayName("刷新 Access Token 失败 - 账号已删除")
    void canNotRefreshTokenWhenAccountDeleted() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, REFRESH_TOKEN_TYPE_VALUE);
        }};
        final String refreshToken = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        final UserDetails userDetails = new UserDetails();
        userDetails.setRefreshToken(refreshToken);
        tokenCache.put(username, userDetails);

        given(userMapper.selectUserByName(username)).willReturn(null);

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("未找到账号");
    }

    @Test
    @DisplayName("刷新 Access Token 失败 - 账号已禁用")
    void canNotRefreshTokenWhenAccountFrozen() {
        // given
        final String username = "测试用户";
        final Map<String, Object> payload = new HashMap<>() {{
            put(ACCOUNT_KEY, username);
            put(TOKEN_TYPE_KEY, REFRESH_TOKEN_TYPE_VALUE);
        }};
        final String refreshToken = JwtUtils.generateJwt(securityConfig.getJwtSigningKey(), payload, 60);

        final UserDetails userDetails = new UserDetails();
        userDetails.setRefreshToken(refreshToken);
        tokenCache.put(username, userDetails);

        final User user = new User();
        user.setEnabled(YesOrNo.NO);
        given(userMapper.selectUserByName(username)).willReturn(user);

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(TokenAuthenticationException.class)
                .hasMessage("账号已禁用");

        assertThat(tokenCache.getIfPresent(username)).isNotNull();
    }
}