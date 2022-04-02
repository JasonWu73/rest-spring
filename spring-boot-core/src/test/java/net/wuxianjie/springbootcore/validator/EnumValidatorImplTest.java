package net.wuxianjie.springbootcore.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import static net.wuxianjie.springbootcore.validator.GroupTest.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = ParameterTestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ComponentScan("net.wuxianjie.springbootcore.rest")
class EnumValidatorImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("枚举参数值错误")
    void itShouldCheckWhenEnumValueIsError() throws Exception {
        // given
        final String enabled = "11";

        // when
        mockMvc.perform(get("/param/enum")
                        .param("enabled", enabled))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("状态值错误"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("枚举参数值正确")
    void itShouldCheckWhenEnumValueIsCorrect() throws Exception {
        // given
        final String enabled = "1";

        // when
        mockMvc.perform(get("/param/enum")
                        .param("enabled", enabled))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当枚举类没有 value 方法")
    void itShouldCheckEnumLackOfValueMethod() throws Exception {
        // given
        final String type = "FACEBOOK";

        // when
        mockMvc.perform(get("/param/enum-no-value-method")
                        .param("type", type))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当枚举类 value 方法执行错误")
    void itShouldCheckEnumValueExecuteError() throws Exception {
        // given
        final String type = "FACEBOOK";

        // when
        mockMvc.perform(get("/param/enum-error-value-method")
                        .param("type", type))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}