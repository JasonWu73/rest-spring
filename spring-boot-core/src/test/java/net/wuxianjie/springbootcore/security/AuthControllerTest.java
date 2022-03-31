package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import net.wuxianjie.springbootcore.rest.*;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static net.wuxianjie.springbootcore.security.WebSecurityConfig.APPLICATION_JSON_UTF8_VALUE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@Import({JsonConfig.class, UrlAndFormRequestParameterConfig.class,
        ExceptionControllerAdvice.class, GlobalErrorController.class,
        GlobalResponseBodyAdvice.class, RestApiConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @MockBean
    private TokenService tokenService;

    @MockBean
    private TokenAuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("无 Token 访问开放 API")
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
    @DisplayName("有 Token 访问开放 API")
    void itShouldCheckWhenRequestPublicApiProvideAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/public")
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value("无需 Token 认证即可访问的开放 API"))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("当有 Token 但 Token 错误时访问开放 API")
    void itShouldCheckWhenRequestPublicApiProvideInvalidAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        String errMsg = "Token 已过期";
        given(authenticationService.authenticate(anyString()))
                .willThrow(new TokenAuthenticationException(errMsg));

        // when
        mockMvc.perform(get("/api/v1/auth-test/public")
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + token))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errMsg));
    }

    @Test
    @DisplayName("当有 Token 但 Token 认证过滤器错误时访问开放 API")
    void itShouldCheckWhenRequestPublicApiProvideAccessTokenButTokenFilterError() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        String errMsg = "执行 Token 认证发生未知错误";
        given(authenticationService.authenticate(anyString()))
                .willThrow(new RuntimeException(errMsg));

        // when
        mockMvc.perform(get("/api/v1/auth-test/public")
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + token))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errMsg));
    }

    @Test
    @DisplayName("无 Token 访问需认证 API")
    void itShouldCheckWhenRequestLoggedInApiButNotProvideAccessToken() throws Exception {
        // given
        // when
        mockMvc.perform(get("/api/v1/auth-test/authenticated"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 认证失败"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("有 Token 访问需认证 API")
    void itShouldCheckWhenRequestLoggedInApiProvideAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/authenticated")
                        .header(HttpHeaders.AUTHORIZATION, " bearer " + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value("只要通过 Token 认证（登录后）即可访问的 API"))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("有 USER 角色授权 Token 访问 USER API")
    void itShouldCheckWhenRequestUserApiProvideUserRoleAccessToken() throws Exception {
        // given
        String msg = StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.USER.value());
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.USER.value());
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(msg))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("既有 USER 又有 ADMIN 角色授权 Token 访问 USER API")
    void itShouldCheckWhenRequestUserApiProvideUserAndAdminRoleAccessToken() throws Exception {
        // given
        String msg = StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.USER.value());
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.USER.value() + ", " + Role.ADMIN.value());
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(msg))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("无授权 Token 访问 USER API")
    void itShouldCheckWhenRequestUserApiProvideNoRoleAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 未授权"));
    }


    @Test
    @DisplayName("有 ADMIN 角色授权 Token 访问 USER API")
    void itShouldCheckWhenRequestUserApiProvideAdminRoleAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.ADMIN.value());
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 未授权"));
    }

    @Test
    @DisplayName("无 Token 访问 USER API")
    void itShouldCheckWhenRequestUserApiProvideNoAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 认证失败"));
    }

    @Test
    @DisplayName("有 ADMIN 角色授权 Token 访问 ADMIN API")
    void itShouldCheckWhenRequestAdminApiProvideAdminRoleAccessToken() throws Exception {
        // given
        String msg = StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.ADMIN.value());
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.ADMIN.value());
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(msg))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("既有 USER 又有 ADMIN 角色授权 Token 访问 ADMIN API")
    void itShouldCheckWhenRequestAdminApiProvideUserAndAdminRoleAccessToken() throws Exception {
        // given
        String msg = StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.ADMIN.value());
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.USER.value() + ", " + Role.ADMIN.value());
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(msg))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("无授权 Token 访问 ADMIN API")
    void itShouldCheckWhenRequestAdminApiProvideNoRoleAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 未授权"));
    }


    @Test
    @DisplayName("有 USER 角色授权 Token 访问 ADMIN API")
    void itShouldCheckWhenRequestAdminApiProvideUserRoleAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.USER.value());
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 未授权"));
    }

    @Test
    @DisplayName("无 Token 访问 ADMIN API")
    void itShouldCheckWhenRequestAdminApiProvideNoAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/admin"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 认证失败"));
    }

    @Test
    @DisplayName("有 USER 角色授权 Token 访问 USER OR ADMIN API")
    void itShouldCheckWhenRequestUserOrAdminApiProvideUserRoleAccessToken() throws Exception {
        // given
        String msg = StrUtil.format("通过 Token 认证且必须拥有 [{}] 或 [{}] 角色才可访问的 API", Role.USER.value(), Role.ADMIN.value());
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.USER.value());
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user-or-admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(msg))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("有 ADMIN 角色授权 Token 访问 USER OR ADMIN API")
    void itShouldCheckWhenRequestUserOrAdminApiProvideAdminRoleAccessToken() throws Exception {
        // given
        String msg = StrUtil.format("通过 Token 认证且必须拥有 [{}] 或 [{}] 角色才可访问的 API", Role.USER.value(), Role.ADMIN.value());
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.ADMIN.value());
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user-or-admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(msg))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("既有 USER 又有 ADMIN 角色授权 Token 访问 USER OR ADMIN API")
    void itShouldCheckWhenRequestUserOrAdminApiProvideUserAndAdminRoleAccessToken() throws Exception {
        // given
        String msg = StrUtil.format("通过 Token 认证且必须拥有 [{}] 或 [{}] 角色才可访问的 API", Role.USER.value(), Role.ADMIN.value());
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        userDetails.setRoles(Role.USER.value() + ", " + Role.ADMIN.value());
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user-or-admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.message").value(msg))
                .andExpect(jsonPath("$.data.username").value(userDetails.getAccountName()));
    }

    @Test
    @DisplayName("无授权 Token 访问 USER OR ADMIN API")
    void itShouldCheckWhenRequestUserOrAdminApiProvideNoRoleAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authenticationService.authenticate(token)).willReturn(userDetails);

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
    @DisplayName("无 Token 访问 USER OR ADMIN API")
    void itShouldCheckWhenRequestUserOrAdminApiProvideNoAccessToken() throws Exception {
        // given
        String token = "fake_access_token";
        UserDetails userDetails = new UserDetails();
        userDetails.setAccountName("测试用户");
        given(authenticationService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/auth-test/user-or-admin"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("Token 认证失败"));
    }
}