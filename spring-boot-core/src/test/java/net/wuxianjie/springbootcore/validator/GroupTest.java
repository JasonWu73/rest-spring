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

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
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
class GroupTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("不符合新增参数要求")
    void itShouldCheckWhenLackOfSaveParam() throws Exception {
        mockMvc.perform(get("/param/save"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                            String body = result
                                    .getResponse()
                                    .getContentAsString(StandardCharsets.UTF_8);

                            assertThat(body)
                                    .contains("启用状态不能为 null")
                                    .contains("名称不能为空");
                        }
                );
    }

    @Test
    @DisplayName("符合新增参数要求")
    void itShouldCheckWhenValidSaveParam() throws Exception {
        mockMvc.perform(get("/param/save")
                        .param("enabled", "1")
                        .param("name", "测试名")
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
    @DisplayName("不符合更新参数要求")
    void itShouldCheckWhenLackOfUpdateParam() throws Exception {
        mockMvc.perform(get("/param/update"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                            String body = result
                                    .getResponse()
                                    .getContentAsString(StandardCharsets.UTF_8);

                            assertThat(body)
                                    .contains("ID 不能为 null")
                                    .contains("名称不能为空");
                        }
                );
    }

    @Test
    @DisplayName("符合更新参数要求")
    void itShouldCheckWhenValidUpdateParam() throws Exception {
        mockMvc.perform(get("/param/update")
                        .param("id", "1")
                        .param("name", "测试名")
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
}