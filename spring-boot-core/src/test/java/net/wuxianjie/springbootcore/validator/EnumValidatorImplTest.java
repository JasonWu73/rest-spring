package net.wuxianjie.springbootcore.validator;

import net.wuxianjie.springbootcore.rest.ExceptionControllerAdvice;
import net.wuxianjie.springbootcore.rest.GlobalResponseBodyAdvice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 吴仙杰
 */
@WebMvcTest(
        controllers = ParamController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import(
        {
                ExceptionControllerAdvice.class,
                GlobalResponseBodyAdvice.class
        }
)
class EnumValidatorImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("枚举值参数值错误")
    void itShouldCheckWhenEnumValueIsError() throws Exception {
        mockMvc.perform(get("/param/enum")
                        .param("enabled", "11")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"状态值错误\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("枚举值参数值正确")
    void itShouldCheckWhenEnumValueIsCorrect() throws Exception {
        mockMvc.perform(get("/param/enum")
                        .param("enabled", "1")
                )
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
    @DisplayName("当枚举类没有 value 方法")
    void itShouldCheckEnumNotHaveValueMethod() throws Exception {
        mockMvc.perform(get("/param/enum-2")
                        .param("type", "FACEBOOK"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("当枚举类 value 方法执行错误")
    void itShouldCheckEnumValueExecuteError() throws Exception {
        mockMvc.perform(get("/param/enum-3")
                        .param("type", "FACEBOOK"))
                .andExpect(status().isOk());
    }
}