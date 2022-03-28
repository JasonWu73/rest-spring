package net.wuxianjie.springbootcore.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 吴仙杰
 */
@WebMvcTest(
        controllers = RestApiController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class GlobalResponseBodyAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Controller 返回值为 Void")
    void itShouldCheckWhenVoidReturn() throws Exception {
        mockMvc.perform(get("/void"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":0," +
                                        "\"errMsg\":null," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("Controller 返回 null 值")
    void itShouldCheckWhenReturnNull() throws Exception {
        mockMvc.perform(get("/result/null"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"服务异常\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("Controller 返回 String 值")
    void itShouldCheckWhenReturnStr() throws Exception {
        mockMvc.perform(get("/result/str"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":0," +
                                        "\"errMsg\":null," +
                                        "\"data\":\"Hello World\"" +
                                        "}"
                        )
                );
    }
}