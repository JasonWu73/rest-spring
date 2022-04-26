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
@WebMvcTest(
  controllers = ParamTestController.class,
  excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@ComponentScan("net.wuxianjie.springbootcore.rest")
class GroupTest {

  static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("重置密码参数校验：不通过")
  void testWhenResetPasswordParamIsInvalid() throws Exception {
    // given
    // when
    mockMvc.perform(get("/param/reset-password"))
      // then
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(result -> {
        String body = result.getResponse().getContentAsString();

        assertThat(body)
          .contains("用户 id 不能为 null")
          .contains("密码不能为空");
      })
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("重置密码参数校验：通过")
  void testWhenResetPasswordIsValid() throws Exception {
    // given
    // when
    mockMvc.perform(get("/param/reset-password")
        .param("userId", "1")
        .param("password", "passw0rd"))
      // then
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("修改密码参数校验：不通过")
  void testWhenUpdatePasswordParamIsInvalid() throws Exception {
    // given
    // when
    mockMvc.perform(get("/param/update-password"))
      // then
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(result -> {
        String body = result.getResponse().getContentAsString();

        assertThat(body)
          .contains("用户 id 不能为 null")
          .contains("密码不能为空")
          .contains("旧密码不能为空");
      })
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("修改密码参数校验：通过")
  void testWhenUpdatePasswordParamIsValid() throws Exception {
    // given
    String id = "1";
    String name = "测试名";

    // when
    mockMvc.perform(get("/param/update-password")
        .param("userId", "1")
        .param("oldPassword", "old-passw0rd")
        .param("password", "new-passw0rd"))
      // then
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.error").value(0))
      .andExpect(jsonPath("$.errMsg").doesNotExist())
      .andExpect(jsonPath("$.data").doesNotExist());
  }
}