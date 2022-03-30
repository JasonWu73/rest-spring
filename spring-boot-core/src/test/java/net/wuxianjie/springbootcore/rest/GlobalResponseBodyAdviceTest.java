package net.wuxianjie.springbootcore.rest;

import net.wuxianjie.springbootcore.shared.CommonValues;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@Import({JsonConfig.class, UrlAndFormRequestParameterConfig.class,
        ExceptionControllerAdvice.class, GlobalErrorController.class,
        GlobalResponseBodyAdvice.class, RestApiConfig.class})
@WebMvcTest(controllers = RestApiController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class GlobalResponseBodyAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Controller 返回值为 Void")
    void itShouldCheckWhenReturnTypeIsVoid() throws Exception {
        // given
        // when
        mockMvc.perform(get("/void"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Controller 返回 null 值")
    void itShouldCheckWhenReturnNull() throws Exception {
        // given
        // when
        mockMvc.perform(get("/result/null"))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("服务异常"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Controller 返回 String 值")
    void itShouldCheckWhenReturnStr() throws Exception {
        // given
        // when
        mockMvc.perform(get("/result/str"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").value("Hello World"));
    }
}