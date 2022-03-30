package net.wuxianjie.springbootcore.rest;

import net.wuxianjie.springbootcore.shared.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@Import({JsonConfig.class, UrlAndFormRequestParameterConfig.class,
        ExceptionControllerAdvice.class, GlobalErrorController.class,
        GlobalResponseBodyAdvice.class, RestApiConfig.class})
@WebMvcTest(controllers = RestApiController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ExceptionControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("API 不支持请求头 Accept 中指定的 MIME 类型，但 Accept 中包含 JSON")
    void itShouldCheckWhenAcceptRequestHeaderContainJsonButApiNotSupported() throws Exception {
        // given
        String errMsg = "API 不支持返回请求头指定的 MIME 类型 [Accept: application/json, application/xml]";
        Class<HttpMediaTypeNotAcceptableException> errorType = HttpMediaTypeNotAcceptableException.class;

        // when
        mockMvc.perform(get("/html")
                        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML))
                // then
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errMsg))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("API 不支持请求头 Accept 中指定的 MIME 类型，且 Accept 中也不包含 JSON")
    void itShouldCheckWhenGetAcceptRequestHeaderNotContainJsonAndApiNotSupported() throws Exception {
        // given
        Class<HttpMediaTypeNotAcceptableException> errorType = HttpMediaTypeNotAcceptableException.class;

        // when
        mockMvc.perform(get("/html")
                        .accept(MediaType.APPLICATION_XML))
                // then
                .andExpect(status().isNotAcceptable())
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_TYPE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("API 不支持请求方法")
    void itShouldCheckWhenRequestMethodApiNotSupported() throws Exception {
        // given
        Class<HttpRequestMethodNotSupportedException> errorType = HttpRequestMethodNotSupportedException.class;

        // when
        mockMvc.perform(post("/html"))
                // then
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("API 不支持 POST 请求方法"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求体内容不合法")
    void itShouldCheckWhenMalformedRequestBody() throws Exception {
        // given
        String reqBody = "{";
        Class<HttpMessageNotReadableException> errorType = HttpMessageNotReadableException.class;

        // when
        mockMvc.perform(post("/body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqBody))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("请求体内容不合法"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求缺少必填参数")
    void itShouldCheckWhenRequestLackOfRequiredParam() throws Exception {
        // given
        Class<MissingServletRequestParameterException> errorType = MissingServletRequestParameterException.class;

        // when
        mockMvc.perform(get("/required"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("缺少必填参数 name"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求参数不合法 @Validated")
    void itShouldCheckWhenRequestParamInvalidByValidatedAnnotation() throws Exception {
        // given
        Class<ConstraintViolationException> errorType = ConstraintViolationException.class;

        // when
        mockMvc.perform(get("/validated"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body)
                            .contains("用户 ID 不能为 null")
                            .contains("用户名不能为空");
                })
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求参数不合法 @Valid")
    void itShouldCheckWhenRequestParamInvalidByValidAnnotation() throws Exception {
        // given
        Class<BindException> errorType = BindException.class;

        // when
        mockMvc.perform(get("/valid"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body)
                            .contains("用户 ID 不能为 null")
                            .contains("用户名不能为空");
                })
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出自定义异常 NotFoundException")
    void itShouldCheckWhenThrowNotFoundException() throws Exception {
        // given
        String type = "not_found";
        Class<NotFoundException> errorType = NotFoundException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("未找到 id 为 x 的数据"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出自定义异常 BadRequestException")
    void itShouldCheckWhenThrowBadRequestException() throws Exception {
        // given
        String type = "bad_request";
        Class<BadRequestException> errorType = BadRequestException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("客户端请求错误"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出自定义异常 ConflictException")
    void itShouldCheckWhenThrowConflictException() throws Exception {
        // given
        String type = "conflict";
        Class<ConflictException> errorType = ConflictException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isConflict())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("已存在相同数据"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出自定义异常 InternalException")
    void itShouldCheckWhenThrowInternalException() throws Exception {
        // given
        String type = "internal";
        Class<InternalException> errorType = InternalException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("服务内部异常"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出 JDBC 异常")
    void itShouldCheckWhenThrowJdbcException() throws Exception {
        // given
        String type = "\tdb\n";
        Class<UncategorizedDataAccessException> errorType = UncategorizedDataAccessException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("数据库操作异常"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出其他异常")
    void itShouldCheckWhenThrowOtherException() throws Exception {
        // given
        Class<Throwable> errorType = Throwable.class;

        // when
        mockMvc.perform(get("/exception"))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("服务异常"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}