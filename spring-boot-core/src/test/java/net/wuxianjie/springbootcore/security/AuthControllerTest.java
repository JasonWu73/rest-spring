package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.rest.ExceptionControllerAdvice;
import net.wuxianjie.springbootcore.rest.GlobalResponseBodyAdvice;
import net.wuxianjie.springbootcore.rest.JsonConfig;
import net.wuxianjie.springbootcore.rest.UrlFormRequestParameterConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 吴仙杰
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(
        {
                JsonConfig.class,
                UrlFormRequestParameterConfig.class,
                ExceptionControllerAdvice.class,
                GlobalResponseBodyAdvice.class
        }
)
class AuthControllerTest {

    @MockBean
    private TokenService tokenService;

    @MockBean
    private TokenAuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("开放 API")
    void itShouldCheckWhenRequestPublicApi() throws Exception {
        mockMvc.perform(get("/api/v1/auth-test/public"))
                .andExpect(status().isOk())
                .andExpect(content().json("" +
                                "{" +
                                "\"error\":0," +
                                "\"data\":{" +
                                "\"message\":\"无需 Token 认证即可访问的开放 API\"," +
                                "\"username\":\"匿名用户\"" +
                                "}" +
                                "}"
                        )
                );
    }

    @Test
    @DisplayName("无 Token 访问需认证 API")
    void itShouldCheckWhenRequestLoggedInButNotProvideAccessToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth-test/authenticated"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("" +
                                "{" +
                                "\"error\":1," +
                                "\"errMsg\":\"Token 认证失败\"" +
                                "}"
                        )
                );
    }

    @Test
    @DisplayName("有 Token 访问需认证 API")
    void itShouldCheckWhenRequestLoggedInProvideAccessToken() throws Exception {
        // given
        String token = "fake_token";
        TokenUserDetails userDetails = new TokenUserDetails();
        when(authenticationService.authenticate(eq(token))).thenReturn(userDetails);

        mockMvc.perform(get("/api/v1/auth-test/authenticated")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":0,\"data\":{" +
                                        "\"message\":\"只要通过 Token 认证（登录后）即可访问的 API\"," +
                                        "\"username\":\"匿名用户\"" +
                                        "}" +
                                        "}"
                        )
                );
    }
}