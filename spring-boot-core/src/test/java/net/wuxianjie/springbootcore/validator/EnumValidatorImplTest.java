package net.wuxianjie.springbootcore.validator;

import net.wuxianjie.springbootcore.rest.*;
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
@WebMvcTest(
        controllers = ParamController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import({
        JsonConfig.class,
        UrlAndFormRequestParameterConfig.class,
        ExceptionControllerAdvice.class,
        GlobalErrorController.class,
        GlobalResponseBodyAdvice.class,
        RestApiConfig.class
})
class EnumValidatorImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("枚举值参数值错误")
    void itShouldCheckWhenEnumValueIsError() throws Exception {
        // given
        String enabled = "11";

        // when
        mockMvc.perform(get("/param/enum")
                        .param("enabled", enabled))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg")
                        .value("状态值错误"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("枚举值参数值正确")
    void itShouldCheckWhenEnumValueIsCorrect() throws Exception {
        // given
        String enabled = "1";

        // when
        mockMvc.perform(get("/param/enum")
                        .param("enabled", enabled))
                // then
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当枚举类没有 value 方法")
    void itShouldCheckEnumNotHaveValueMethod() throws Exception {
        // given
        String type = "FACEBOOK";

        // when
        mockMvc.perform(get("/param/enum-no-value-method")
                        .param("type", type))
                // then
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当枚举类 value 方法执行错误")
    void itShouldCheckEnumValueExecuteError() throws Exception {
        // given
        String type = "FACEBOOK";

        // when
        mockMvc.perform(get("/param/enum-error-value-method")
                        .param("type", type))
                // then
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}