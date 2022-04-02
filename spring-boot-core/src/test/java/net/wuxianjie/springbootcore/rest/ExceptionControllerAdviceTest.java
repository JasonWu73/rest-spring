package net.wuxianjie.springbootcore.rest;

import net.wuxianjie.springbootcore.shared.exception.*;
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
@WebMvcTest(controllers = ApiTestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ExceptionControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Accept 中包含 JSON MIME 类型，但目标 API 不支持")
    void itShouldCheckWhenAcceptRequestHeaderContainJsonButApiNotSupported() throws Exception {
        // given
        final String errMsg = "API 不支持返回请求头指定的 MIME 类型 [Accept: application/json, application/xml]";
        final Class<HttpMediaTypeNotAcceptableException> errorType = HttpMediaTypeNotAcceptableException.class;

        // when
        mockMvc.perform(get("/html")
                        .accept(APPLICATION_JSON, APPLICATION_XML))
                // then
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errMsg))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Accept 不包含 JSON MIME 类型，且目标 API 也不支持")
    void itShouldCheckWhenGetAcceptRequestHeaderNotContainJsonAndApiNotSupported() throws Exception {
        // given
        final Class<HttpMediaTypeNotAcceptableException> errorType = HttpMediaTypeNotAcceptableException.class;

        // when
        mockMvc.perform(get("/html")
                        .accept(APPLICATION_XML))
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
        final Class<HttpRequestMethodNotSupportedException> errorType = HttpRequestMethodNotSupportedException.class;

        // when
        mockMvc.perform(post("/html"))
                // then
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("API 不支持 POST 请求方法"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求体内容不合法")
    void itShouldCheckWhenMalformedRequestBody() throws Exception {
        // given
        final String reqBody = "{";
        final Class<HttpMessageNotReadableException> errorType = HttpMessageNotReadableException.class;

        // when
        mockMvc.perform(post("/body")
                        .contentType(APPLICATION_JSON)
                        .content(reqBody))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("请求体内容不合法"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求缺少必填参数")
    void itShouldCheckWhenRequestLackOfRequiredParam() throws Exception {
        // given
        final Class<MissingServletRequestParameterException> errorType = MissingServletRequestParameterException.class;

        // when
        mockMvc.perform(get("/required"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("缺少必填参数 name"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求参数不合法 @Validated")
    void itShouldCheckWhenRequestParamInvalidByValidatedAnnotation() throws Exception {
        // given
        final Class<ConstraintViolationException> errorType = ConstraintViolationException.class;

        // when
        mockMvc.perform(get("/validated"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(result -> {
                    final String body = result.getResponse().getContentAsString();
                    assertThat(body)
                            .contains("用户 id 不能为 null")
                            .contains("用户名不能为空");
                })
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求参数不合法 @Valid")
    void itShouldCheckWhenRequestParamInvalidByValidAnnotation() throws Exception {
        // given
        final Class<BindException> errorType = BindException.class;

        // when
        mockMvc.perform(get("/valid"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(result -> {
                    final String body = result.getResponse().getContentAsString();
                    assertThat(body)
                            .contains("用户 id 不能为 null")
                            .contains("用户名不能为空");
                })
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出自定义异常 NotFoundException")
    void itShouldCheckWhenThrowNotFoundException() throws Exception {
        // given
        final String type = "not_found";
        final Class<NotFoundException> errorType = NotFoundException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("未找到 id 为 x 的数据"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出自定义异常 BadRequestException")
    void itShouldCheckWhenThrowBadRequestException() throws Exception {
        // given
        final String type = "bad_request";
        final Class<BadRequestException> errorType = BadRequestException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("客户端请求错误"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出自定义异常 DataConflictException")
    void itShouldCheckWhenThrowDataConflictException() throws Exception {
        // given
        final String type = "conflict";
        final Class<DataConflictException> errorType = DataConflictException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isConflict())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("已存在相同数据"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出自定义异常 InternalException")
    void itShouldCheckWhenThrowInternalException() throws Exception {
        // given
        final String type = "internal";
        final Class<InternalException> errorType = InternalException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("服务内部异常"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出自定义异常 ExternalException")
    void itShouldCheckWhenThrowExternalException() throws Exception {
        // given
        final String type = " external";
        final Class<ExternalException> errorType = ExternalException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("外部 API 不可用"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出 JDBC 异常")
    void itShouldCheckWhenThrowJdbcException() throws Exception {
        // given
        final String type = "\tdb\n";
        final Class<UncategorizedDataAccessException> errorType = UncategorizedDataAccessException.class;

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("数据库操作异常"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("当程序抛出其他异常")
    void itShouldCheckWhenThrowOtherException() throws Exception {
        // given
        final Class<Throwable> errorType = Throwable.class;

        // when
        mockMvc.perform(get("/exception"))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("服务异常"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}