package net.wuxianjie.springbootcore.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        TokenData tokenData = new TokenData(
                1800,
                "access_token",
                "refresh_token"
        );
        Map<String, Object> paramMap = new HashMap<>() {
            {
                put("accountName", "吴仙杰");
                put("accountPassword", "213");
            }
        };

        // when
        when(tokenService.getToken(any(), any())).thenReturn(tokenData);

        // then
        mockMvc.perform(post("/api/v1/access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramMap))
                )
                .andExpect(status().isOk())
                .andExpect(content().json("" +
                                "{" +
                                "\"error\":0," +
                                "\"data\":{" +
                                "\"expiresIn\":1800," +
                                "\"accessToken\":\"access_token\"," +
                                "\"refreshToken\":\"refresh_token\"" +
                                "}" +
                                "}"
                        )
                );
    }

    @Test
    @DisplayName("刷新 Access Token")
    void canRefreshToken() throws Exception {
        // given
        TokenData tokenData = new TokenData(
                1800,
                "access_token",
                "refresh_token"
        );

        // when
        when(tokenService.refreshToken(any())).thenReturn(tokenData);

        // then
        mockMvc.perform(get("/api/v1/refresh-token/token_123"))
                .andExpect(status().isOk())
                .andExpect(content().json("" +
                                "{" +
                                "\"error\":0," +
                                "\"data\":{" +
                                "\"expiresIn\":1800," +
                                "\"accessToken\":\"access_token\"," +
                                "\"refreshToken\":\"refresh_token\"" +
                                "}" +
                                "}"
                        )
                );
    }
}