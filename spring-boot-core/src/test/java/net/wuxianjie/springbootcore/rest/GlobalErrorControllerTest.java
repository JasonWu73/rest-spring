package net.wuxianjie.springbootcore.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.RequestDispatcher;

import static net.wuxianjie.springbootcore.rest.GlobalResponseBodyAdvice.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@WebMvcTest(
  controllers = GlobalErrorController.class,
  excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class GlobalErrorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("404 Spring Boot 白标签错误页")
  void testWhen404WhiteLabelErrorPage() throws Exception {
    // given
    // when
    mockMvc.perform(get("/error")
        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value()))
      // then
      .andExpect(status().isNotFound())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("Not Found"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("500 Spring Boot 白标签错误页")
  void testWhen500WhiteLabelErrorPage() throws Exception {
    // given
    // when
    mockMvc.perform(get("/error"))
      // then
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("None"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }
}