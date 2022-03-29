package net.wuxianjie.springbootcore.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.wuxianjie.springbootcore.rest.*;
import net.wuxianjie.springbootcore.shared.CommonValues;
import net.wuxianjie.springbootcore.shared.TokenAuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@Import({
        JsonConfig.class,
        UrlAndFormRequestParameterConfig.class,
        ExceptionControllerAdvice.class,
        GlobalErrorController.class,
        GlobalResponseBodyAdvice.class,
        RestApiConfig.class
})
@SpringBootTest
@AutoConfigureMockMvc
class TokenControllerTest {

    @MockBean
    private TokenService tokenService;

    @MockBean
    private TokenAuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("获取 Access Token")
    void canGetToken() throws Exception {
        // given
        String accessToken = "fake_access_token";
        String refreshToken = "fake_refresh_token";
        TokenData tokenData = new TokenData(
                1800,
                accessToken,
                refreshToken
        );
        String accountName = "吴仙杰";
        String password = "213";
        Map<String, Object> params = new HashMap<>() {{
            put("accountName", accountName);
            put("accountPassword", password);
        }};

        given(tokenService.getToken(accountName, password)).willReturn(tokenData);

        // when
        mockMvc.perform(post("/api/v1/access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                // then
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.expiresIn")
                        .value(1800))
                .andExpect(jsonPath("$.data.accessToken").value(accessToken))
                .andExpect(jsonPath("$.data.refreshToken").value(refreshToken));
    }

    @Test
    @DisplayName("因用户名或密码错误而无法获取 Access Token")
    void canNotGetTokenWhenInvalidAccountNameOrPassword() throws Exception {
        // given
        String accountName = "吴仙杰";
        String password = "213";
        Map<String, Object> params = new HashMap<>() {{
            put("accountName", accountName);
            put("accountPassword", password);
        }};

        given(tokenService.getToken(anyString(), anyString()))
                .willThrow(new TokenAuthenticationException("用户名或密码错误"));

        // when
        mockMvc.perform(post("/api/v1/access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg")
                        .value("用户名或密码错误"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("刷新 Access Token")
    void canRefreshToken() throws Exception {
        // given
        String accessToken = "fake_access_token";
        String refreshToken = "fake_refresh_token";
        TokenData tokenData = new TokenData(
                1800,
                accessToken,
                refreshToken
        );

        given(tokenService.refreshToken(refreshToken)).willReturn(tokenData);

        // when
        mockMvc.perform(get("/api/v1/refresh-token/" + refreshToken))
                // then
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data.expiresIn")
                        .value(1800))
                .andExpect(jsonPath("$.data.accessToken").value(accessToken))
                .andExpect(jsonPath("$.data.refreshToken").value(refreshToken));
    }

    @Test
    @DisplayName("因 Token 无效而无法刷新 Access Token")
    void canNotRefreshTokenWhenInvalidToken() throws Exception {
        // given
        String accessToken = "fake_access_token";
        String refreshToken = "fake_refresh_token";
        TokenData tokenData = new TokenData(
                1800,
                accessToken,
                refreshToken
        );

        given(tokenService.refreshToken(anyString()))
                .willThrow(new TokenAuthenticationException("Token 已过期"));

        // when
        mockMvc.perform(get("/api/v1/refresh-token/" + refreshToken))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg")
                        .value("Token 已过期"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}