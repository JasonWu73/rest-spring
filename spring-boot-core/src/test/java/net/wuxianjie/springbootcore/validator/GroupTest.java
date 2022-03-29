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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
@WebMvcTest(
        controllers = ParamController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class GroupTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("不符合新增参数要求")
    void itShouldCheckWhenLackOfSaveParam() throws Exception {
        // given
        // when
        mockMvc.perform(get("/param/save"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();

                    assertThat(body)
                            .contains("启用状态不能为 null")
                            .contains("名称不能为空");
                })
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("符合新增参数要求")
    void itShouldCheckWhenValidSaveParam() throws Exception {
        // given
        String enabled = "1";
        String name = "测试名";

        // when
        mockMvc.perform(get("/param/save")
                        .param("enabled", enabled)
                        .param("name", name))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("不符合更新参数要求")
    void itShouldCheckWhenLackOfUpdateParam() throws Exception {
        // given
        // when
        mockMvc.perform(get("/param/update"))
                // ghen
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();

                    assertThat(body)
                            .contains("ID 不能为 null")
                            .contains("名称不能为空");
                })
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("符合更新参数要求")
    void itShouldCheckWhenValidUpdateParam() throws Exception {
        // given
        String id = "1";
        String name = "测试名";

        // when
        mockMvc.perform(get("/param/update")
                        .param("id", id)
                        .param("name", name))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}