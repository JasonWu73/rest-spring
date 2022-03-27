package net.wuxianjie.springbootcore.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = RestApiController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class GlobalResponseBodyAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenVoidShouldReturnJson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/void"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":0," +
                        "\"errMsg\":null," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenNullShouldReturn500HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/result/null"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"服务异常\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenStringShouldReturnJson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/result/str"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":0," +
                        "\"errMsg\":null," +
                        "\"data\":\"Hello World\"" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }
}