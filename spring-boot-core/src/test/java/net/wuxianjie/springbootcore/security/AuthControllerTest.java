package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
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

  @MockBean
  private TokenService tokenService;

  @MockBean
  private TokenAuthenticationService authService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("访问网页图标")
  void testWhenRequestFaviconUri() throws Exception {
    // given
    // when
    mockMvc.perform(get(FAVICON_PATH))
      // then
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  @DisplayName("访问开放 API：无 Token")
  void testWhenRequestPublicApiAndNotProvideAccessToken() throws Exception {
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
  @DisplayName("访问开放 API：有 Token")
  void testWhenRequestPublicApiButProvideAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");

    given(authService.authenticate(token)).willReturn(user);

    // when
    mockMvc.perform(get("/api/v1/auth-test/public")
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data.message").value("无需 Token 认证即可访问的开放 API"))
      .andExpect(jsonPath("$.data.username").value(user.getAccountName()));
  }

  @Test
  @DisplayName("访问开放 API：有过期 Token")
  void testWhenRequestPublicApiButProvideExpiredAccessToken() throws Exception {
    // given
    String token = "fake_access_token";
    String errMsg = "Token 已过期";

    given(authService.authenticate(token)).willThrow(new TokenAuthenticationException(errMsg));

    // when
    mockMvc.perform(get("/api/v1/auth-test/public")
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
      // then
      .andExpect(status().isUnauthorized())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value(errMsg));
  }

  @Test
  @DisplayName("访问开放 API：Token 认证过滤器错误")
  void testWhenRequestPublicApiButTokenFilterThrowEexception() throws Exception {
    // given
    String token = "fake_access_token";

    given(authService.authenticate(token)).willThrow(new RuntimeException("执行 Token 认证发生未知错误"));

    // when
    mockMvc.perform(get("/api/v1/auth-test/public")
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
      // then
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("Token 认证异常"));
  }

  @Test
  @DisplayName("访问受保护 API：无 Token")
  void testWhenRequestProtectedApiButNotProvideAccessToken() throws Exception {
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
  @DisplayName("访问受保护 API：有 Token")
  void testWhenRequestProtectedApiAndProvideAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");

    given(authService.authenticate(token)).willReturn(user);

    // when
    mockMvc.perform(get("/api/v1/auth-test/protected")
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data.message").value("只要通过 Token 认证（登录后）即可访问的 API"))
      .andExpect(jsonPath("$.data.username").value(user.getAccountName()));
  }

  @Test
  @DisplayName("访问 USER API：有 USER 角色授权 Token")
  void testWhenRequestUserApiAndProvideUserRoleAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");
    user.setRoles(Role.USER.value());

    given(authService.authenticate(token)).willReturn(user);

    // when
    mockMvc.perform(get("/api/v1/auth-test/user")
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data.message").value(StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.USER.value())))
      .andExpect(jsonPath("$.data.username").value(user.getAccountName()));
  }

  @Test
  @DisplayName("访问 USER API：同时有 USER 和 ADMIN 角色授权 Token")
  void testWhenRequestUserApiAndProvideUserAdminRoleAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");
    user.setRoles(Role.USER.value() + ", " + Role.ADMIN.value());

    given(authService.authenticate(token)).willReturn(user);

    // when
    mockMvc.perform(get("/api/v1/auth-test/user")
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data.message").value(StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.USER.value())))
      .andExpect(jsonPath("$.data.username").value(user.getAccountName()));
  }

  @Test
  @DisplayName("访问 USER API：无授权 Token")
  void testWhenRequestUserApiButProvideNoRoleAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");

    given(authService.authenticate(token)).willReturn(user);

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
  @DisplayName("访问 USER API：有 ADMIN 角色授权 Token")
  void testWhenRequestUserApiProvideAdminRoleAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");
    user.setRoles(Role.ADMIN.value());

    given(authService.authenticate(token)).willReturn(user);

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
  @DisplayName("访问 USER API：无 Token")
  void testWhenRequestUserApiButNotProvideAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails userDetails = new UserDetails();
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
  @DisplayName("访问 ADMIN API：有 ADMIN 角色授权 Token")
  void testWhenRequestAdminApiAndProvideAdminRoleAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");
    user.setRoles(Role.ADMIN.value());

    given(authService.authenticate(token)).willReturn(user);

    // when
    mockMvc.perform(get("/api/v1/auth-test/admin")
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data.message").value(StrUtil.format("通过 Token 认证且必须拥有 [{}] 角色才可访问的 API", Role.ADMIN.value())))
      .andExpect(jsonPath("$.data.username").value(user.getAccountName()));
  }

  @Test
  @DisplayName("访问 ADMIN API：有 USER 角色授权 Token")
  void testWhenRequestAdminApiButProvideUserRoleAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");
    user.setRoles(Role.USER.value());

    given(authService.authenticate(token)).willReturn(user);

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
  @DisplayName("访问 USER OR ADMIN API：有 USER 角色授权 Token")
  void testWhenRequestUserOrAdminApiProvideUserRoleAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");
    user.setRoles(Role.USER.value());

    given(authService.authenticate(token)).willReturn(user);

    // when
    mockMvc.perform(get("/api/v1/auth-test/user-or-admin")
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data.message").value(StrUtil.format("通过 Token 认证且必须拥有 [{}] 或 [{}] 角色才可访问的 API", Role.USER.value(), Role.ADMIN.value())))
      .andExpect(jsonPath("$.data.username").value(user.getAccountName()));
  }

  @Test
  @DisplayName("访问 USER OR ADMIN API：无授权 Token")
  void testWhenRequestUserOrAdminApiProvideNoRoleAccessToken() throws Exception {
    // given
    String token = "fake_access_token";

    UserDetails user = new UserDetails();
    user.setAccountName("测试用户");

    given(authService.authenticate(token)).willReturn(user);

    // when
    mockMvc.perform(get("/api/v1/auth-test/user-or-admin")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
      // then
      .andExpect(status().isForbidden())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("Token 未授权"));
  }
}