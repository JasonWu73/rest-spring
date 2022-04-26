package net.wuxianjie.springbootcore.rest;

import net.wuxianjie.springbootcore.exception.InternalException;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartException;

import javax.validation.ConstraintViolationException;

import static net.wuxianjie.springbootcore.rest.GlobalResponseBodyAdvice.APPLICATION_JSON_UTF8_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@WebMvcTest(
  controllers = ApiTestController.class,
  excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class ExceptionControllerAdviceTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("API 不支持当前请求的 MIME 类型：Accept 中包含 JSON")
  void testWhenMimeTypeNotAcceptableButJsonRequest() throws Exception {
    // given
    // when
    mockMvc.perform(get("/html")
        .accept(APPLICATION_JSON, APPLICATION_XML))
      // then
      .andExpect(status().isNotAcceptable())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(HttpMediaTypeNotAcceptableException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("API 不支持当前请求的 MIME 类型 [Accept: application/json, application/xml]"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("API 不支持当前请求的 MIME 类型：Accept 中不包含 JSON")
  void testWhenMimeTypeNotAcceptableAndNotJsonRequest() throws Exception {
    // given
    // when
    mockMvc.perform(get("/html")
        .accept(APPLICATION_XML))
      // then
      .andExpect(status().isNotAcceptable())
      .andExpect(header().doesNotExist(HttpHeaders.CONTENT_TYPE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(HttpMediaTypeNotAcceptableException.class))
      .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  @DisplayName("API 不支持请求方法")
  void testWhenRequestMethodNotSupported() throws Exception {
    // given
    // when
    mockMvc.perform(post("/html"))
      // then
      .andExpect(status().isMethodNotAllowed())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(HttpRequestMethodNotSupportedException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("API 不支持 POST 请求方法"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("API 要求必须为 Multipart Request")
  void testWhenNotMultipartRequestBody() throws Exception {
    // given
    // when
    mockMvc.perform(post("/upload")
        .contentType(APPLICATION_JSON)
        .content("{}"))
      // then
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(MultipartException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("API 请求必须为 Multipart Request"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("请求体内容不合法")
  void testWhenMalformedRequestBody() throws Exception {
    // given
    // when
    mockMvc.perform(post("/body")
        .contentType(APPLICATION_JSON)
        .content("{"))
      // then
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(HttpMessageNotReadableException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("请求体内容不合法"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("请求缺少必填参数")
  void testWhenLackOfRequiredRequestParam() throws Exception {
    // given
    // when
    mockMvc.perform(get("/required"))
      // then
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(MissingServletRequestParameterException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("缺少必填参数 userId"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("请求参数不合法：@Validated")
  void testWhenInvalidRequestParamByValidatedAnnotation() throws Exception {
    // given
    // when
    mockMvc.perform(get("/validated"))
      // then
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(ConstraintViolationException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(result -> {
        String body = result.getResponse().getContentAsString();

        assertThat(body)
          .contains("用户 id 不能为 null")
          .contains("用户名不能为空");
      })
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("请求参数不合法：@Valid")
  void testWhenInvalidRequestParamByValidAnnotation() throws Exception {
    // given
    // when
    mockMvc.perform(get("/valid"))
      // then
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(BindException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(result -> {
        String body = result.getResponse().getContentAsString();

        assertThat(body)
          .contains("用户 id 不能为 null")
          .contains("用户名不能为空");
      })
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("JDBC 操作异常")
  void testWhenThrowJdbcException() throws Exception {
    // given
    // when
    mockMvc.perform(get("/exception")
        .param("type", "db"))
      // then
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(UncategorizedDataAccessException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("数据库操作异常"))
      .andExpect(jsonPath("$.data").doesNotExist());

  }

  @Test
  @DisplayName("自定义异常：NotFoundException")
  void testWhenThrowNotFoundCustomException() throws Exception {
    // given
    // when
    mockMvc.perform(get("/exception")
        .param("type", "not_found"))
      // then
      .andExpect(status().isNotFound())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(NotFoundException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("未找到 id 为 x 的数据"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("自定义异常：InternalException")
  void testWhenThrowInternalCustomException() throws Exception {
    // given
    // when
    mockMvc.perform(get("/exception")
        .param("type", "internal"))
      // then
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(InternalException.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("服务内部异常"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("程序内其他未自定义处理异常")
  void testWhenThrowOtherException() throws Exception {
    // given
    // when
    mockMvc.perform(get("/exception"))
      // then
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
      .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(Throwable.class))
      .andExpect(jsonPath("$.error").value(1))
      .andExpect(jsonPath("$.errMsg").value("服务异常"))
      .andExpect(jsonPath("$.data").doesNotExist());
  }
}