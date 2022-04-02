package net.wuxianjie.springbootcore.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = ParamTestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ComponentScan("net.wuxianjie.springbootcore.rest")
class GroupTest {

    static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

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
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(result -> {
                    final String body = result.getResponse().getContentAsString();
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
        final String enabled = "1";
        final String name = "测试名";

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
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(result -> {
                    final String body = result.getResponse().getContentAsString();
                    assertThat(body)
                            .contains("id 不能为 null")
                            .contains("名称不能为空");
                })
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("符合更新参数要求")
    void itShouldCheckWhenValidUpdateParam() throws Exception {
        // given
        final String id = "1";
        final String name = "测试名";

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