package net.wuxianjie.springbootcore.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static net.wuxianjie.springbootcore.security.WebSecurityConfig.APPLICATION_JSON_UTF8_VALUE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 测试用户
 */
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("net.wuxianjie.springbootcore.rest")
class TokenControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private TokenService tokenService;

  @MockBean
  private TokenAuthenticationService authService;

  @Test
  @DisplayName("获取 Access Token")
  void canGetToken() throws Exception {
    // given
    String account = "测试用户";
    String password = "123";
    Map<String, Object> params = new HashMap<>() {{
      put("account", account);
      put("password", password);
    }};

    String accessToken = "fake_access_token";
    String refreshToken = "fake_refresh_token";
    int expiresIn = 1800;
    TokenData tokenData = new TokenData(expiresIn, accessToken, refreshToken);

    given(tokenService.getToken(account, password)).willReturn(tokenData);

    given(authService.authenticate(accessToken)).willThrow(new RuntimeException("身份认证代码异常"));

    // when
    mockMvc.perform(post("/api/v1/access-token")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(params)))
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data.expiresIn").value(expiresIn))
      .andExpect(jsonPath("$.data.accessToken").value(accessToken))
      .andExpect(jsonPath("$.data.refreshToken").value(refreshToken));
  }

  @Test
  @DisplayName("账号或密码错误")
  void canNotGetTokenWhenAccountNameNotExists() throws Exception {
    // given
    String account = "测试用户";
    String password = "123";
    Map<String, Object> params = new HashMap<>() {{
      put("account", account);
      put("password", password);
    }};

    String errMsg = "账号或密码错误";

    given(tokenService.getToken(anyString(), anyString())).willThrow(new TokenAuthenticationException(errMsg));

    // when
    mockMvc.perform(post("/api/v1/access-token")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(params)))
      // then
      .andExpect(status().isUnauthorized())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value(errMsg))
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("刷新 Access Token")
  void canRefreshToken() throws Exception {
    // given
    String accessToken = "fake_access_token";
    String refreshToken = "fake_refresh_token";
    TokenData tokenData = new TokenData(1800, accessToken, refreshToken);

    given(tokenService.refreshToken(refreshToken)).willReturn(tokenData);

    given(authService.authenticate(accessToken)).willThrow(new RuntimeException("身份认证代码异常"));

    // when
    mockMvc.perform(get("/api/v1/refresh-token/" + refreshToken))
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data.expiresIn").value(1800))
      .andExpect(jsonPath("$.data.accessToken").value(accessToken))
      .andExpect(jsonPath("$.data.refreshToken").value(refreshToken));
  }

  @Test
  @DisplayName("Token 已过期")
  void canNotRefreshTokenWhenInvalidToken() throws Exception {
    // given
    String refreshToken = "fake_refresh_token";
    String errMsg = "Token 已过期";

    given(tokenService.refreshToken(anyString())).willThrow(new TokenAuthenticationException(errMsg));

    // when
    mockMvc.perform(get("/api/v1/refresh-token/" + refreshToken))
      // then
      .andExpect(status().isUnauthorized())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value(errMsg))
      .andExpect(jsonPath("$.data").doesNotExist());
  }
}