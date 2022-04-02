package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static net.wuxianjie.springbootcore.security.TokenAuthenticationFilter.BEARER_PREFIX;
import static net.wuxianjie.springbootcore.security.WebSecurityConfig.APPLICATION_JSON_UTF8_VALUE;
import static net.wuxianjie.springbootcore.security.WebSecurityConfig.FAVICON_PATH;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("net.wuxianjie.springbootcore.rest")
class AuthControllerTest {

    @SuppressWarnings("unused")
    @MockBean
    private TokenService tokenService;
    @MockBean
    private TokenAuthenticationService authService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("网页图标 URI - 无 Token")
    void itShouldCheckWhenRequestFaviconUri() throws Exception {
        // given
        // when
        mockMvc.perform(get(FAVICON_PATH))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("开放 API - 无 Token")
    void itShouldCheckWhenRequestPublicApi() throws Exception {
        // given
        // when
        mockMvc.perform(get("/api/v1/auth-test/public"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value("无需 Token 认证即可访问的开放 API"))
                .andExpect(jsonPath("$.data.username").value("匿名用户"));
    }

    @Test
    @DisplayName("开放 API - 有 Token")
    void itShouldCheckWhenRequestPublicApiProvideAccessToken() throws Exception {
        // given
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/public")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value("无需 Token 认证即可访问的开放 API"))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("开放 API - Token 已过期")
    void itShouldCheckWhenRequestPublicApiProvideExpiredAccessToken() throws Exception {
        // given
        final String token = "fake_access_token";
        final String errorMessage = "Token 已过期";
        given(authService.authenticate(token)).willThrow(new TokenAuthenticationException(errorMessage));

        // when
        mockMvc.perform(get("/api/v1/auth-test/public")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage));
    }

    @Test
    @DisplayName("开放 API - Token 认证过滤器错误")
    void itShouldCheckWhenRequestPublicApiButTokenFilterError() throws Exception {
        // given
        final String token = "fake_access_token";
        final String errorMessage = "执行 Token 认证发生未知错误";
        given(authService.authenticate(token)).willThrow(new RuntimeException(errorMessage));

        // when
        mockMvc.perform(get("/api/v1/auth-test/public")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage));
    }

    @Test
    @DisplayName("受保护 API - 无 Token")
    void itShouldCheckWhenRequestProtectedApiButNotProvideAccessToken() throws Exception {
        // given
        // when
        mockMvc.perform(get("/api/v1/auth-test/protected"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 认证失败"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("受保护 API - 有 Token")
    void itShouldCheckWhenRequestProtectedApiProvideAccessToken() throws Exception {
        // given
        final String message = "只要通过 Token 认证（登录后）即可访问的 API";
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/protected")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(message))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("USER API - 有 USER 角色授权 Token")
    void itShouldCheckWhenRequestUserApiProvideUserRoleAccessToken() throws Exception {
        // given
        final String message = StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.USER.value());
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.USER.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(message))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("USER API - 同时有 USER 和 ADMIN 角色授权 Token")
    void itShouldCheckWhenRequestUserApiProvideUserAndAdminRoleAccessToken() throws Exception {
        // given
        final String msg = StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.USER.value());
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.USER.value() + ", " + Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(msg))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("USER API - 无授权 Token 访问")
    void itShouldCheckWhenRequestUserApiProvideNoRoleAccessToken() throws Exception {
        // given
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 未授权"));
    }


    @Test
    @DisplayName("USER API - 有 ADMIN 角色授权 Token")
    void itShouldCheckWhenRequestUserApiProvideAdminRoleAccessToken() throws Exception {
        // given
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 未授权"));
    }

    @Test
    @DisplayName("USER API - 无 Token")
    void itShouldCheckWhenRequestUserApiProvideNoAccessToken() throws Exception {
        // given
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 认证失败"));
    }

    @Test
    @DisplayName("ADMIN API - 有 ADMIN 角色授权 Token")
    void itShouldCheckWhenRequestAdminApiProvideAdminRoleAccessToken() throws Exception {
        // given
        final String msg = StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.ADMIN.value());
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/admin")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(msg))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("ADMIN API - 无授权 Token")
    void itShouldCheckWhenRequestAdminApiProvideNoRoleAccessToken() throws Exception {
        // given
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/admin")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 未授权"));
    }

    @Test
    @DisplayName("ADMIN API - 无 Token")
    void itShouldCheckWhenRequestAdminApiProvideNoAccessToken() throws Exception {
        // given
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/admin"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 认证失败"));
    }

    @Test
    @DisplayName("USER OR ADMIN API - 有 USER 角色授权 Token")
    void itShouldCheckWhenRequestUserOrAdminApiProvideUserRoleAccessToken() throws Exception {
        // given
        final String message = StrUtil.format("通过 Token 认证且必须拥有 [{}] 或 [{}] 角色才可访问的 API",
                Role.USER.value(), Role.ADMIN.value());
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.USER.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user-or-admin")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(message))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("USER OR ADMIN API - 有 ADMIN 角色授权 Token")
    void itShouldCheckWhenRequestUserOrAdminApiProvideAdminRoleAccessToken() throws Exception {
        // given
        final String message = StrUtil.format("通过 Token 认证且必须拥有 [{}] 或 [{}] 角色才可访问的 API",
                Role.USER.value(), Role.ADMIN.value());
        final String token = "fake_access_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user-or-admin")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(message))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("USER OR ADMIN API - 无授权 Token")
    void itShouldCheckWhenRequestUserOrAdminApiProvideNoRoleAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user-or-admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 未授权"));
    }

    @Test
    @DisplayName(" USER OR ADMIN API - 无 Token")
    void itShouldCheckWhenRequestUserOrAdminApiProvideNoAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user-or-admin"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 认证失败"));
    }
}