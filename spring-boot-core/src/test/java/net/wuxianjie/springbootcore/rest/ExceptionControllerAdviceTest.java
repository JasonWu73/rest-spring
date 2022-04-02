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
    @DisplayName("API MIME 类型不支持 - Accept 中包含 JSON")
    void requestNotAcceptableWhenApiNotSupportedButAcceptRequestHeaderContainsJson() throws Exception {
        // given
        final Class<HttpMediaTypeNotAcceptableException> errorType = HttpMediaTypeNotAcceptableException.class;
        final String errorMessage = "API 不支持返回请求头指定的 MIME 类型 [Accept: application/json, application/xml]";

        // when
        mockMvc.perform(get("/html")
                        .accept(APPLICATION_JSON, APPLICATION_XML))
                // then
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("API MIME 类型不支持 - Accept 中不包含 JSON")
    void requestNotAcceptableWhenApiNotSupportedAndAcceptRequestHeaderNotContainsJson() throws Exception {
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
    void requestMethodNotAllowedWhenApiNotSupported() throws Exception {
        // given
        final Class<HttpRequestMethodNotSupportedException> errorType = HttpRequestMethodNotSupportedException.class;
        final String errorMessage = "API 不支持 POST 请求方法";

        // when
        mockMvc.perform(post("/html"))
                // then
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求体内容不合法")
    void badRequestWhenMalformedRequestBody() throws Exception {
        // given
        final String requestBody = "{";
        final Class<HttpMessageNotReadableException> errorType = HttpMessageNotReadableException.class;
        final String errorMessage = "请求体内容不合法";

        // when
        mockMvc.perform(post("/body")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求缺少必填参数")
    void badRequestWhenLackOfRequiredRequestParameter() throws Exception {
        // given
        final Class<MissingServletRequestParameterException> errorType = MissingServletRequestParameterException.class;
        final String errorMessage = "缺少必填参数 name";

        // when
        mockMvc.perform(get("/required"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("请求参数不合法 - @Validated")
    void badRequestWhenInvalidRequestParameterByValidatedAnnotation() throws Exception {
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
    @DisplayName("请求参数不合法 - @Valid")
    void badRequestWhenInvalidRequestParameterByValidAnnotation() throws Exception {
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
    @DisplayName("自定义异常 - NotFoundException")
    void notFoundWhenThrowCustomException() throws Exception {
        // given
        final String type = "not_found";
        final Class<NotFoundException> errorType = NotFoundException.class;
        final String errorMessage = "未找到 id 为 x 的数据";

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("自定义异常 -  BadRequestException")
    void badRequestWhenThrowCustomException() throws Exception {
        // given
        final String type = "bad_request";
        final Class<BadRequestException> errorType = BadRequestException.class;
        final String errorMessage = "客户端请求错误";

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("自定义异常 - ConflictException")
    void conflictWhenThrowCustomException() throws Exception {
        // given
        final String type = "conflict";
        final Class<ConflictException> errorType = ConflictException.class;
        final String errorMessage = "已存在相同数据";

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isConflict())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("自定义异常 - InternalException")
    void internalServerErrorWhenThrowCustomException() throws Exception {
        // given
        final String type = "internal";
        final Class<InternalException> errorType = InternalException.class;
        final String errorMessage = "服务内部异常";

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("自定义异常 - ExternalException")
    void serviceUnavailableWhenThrowCustomException() throws Exception {
        // given
        final String type = " external";
        final Class<ExternalException> errorType = ExternalException.class;
        final String errorMessage = "外部 API 不可用";

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("JDBC 操作异常")
    void internalServerErrorWhenThrowJdbcException() throws Exception {
        // given
        final String type = "\tdb\n";
        final Class<UncategorizedDataAccessException> errorType = UncategorizedDataAccessException.class;
        final String errorMessage = "数据库操作异常";

        // when
        mockMvc.perform(get("/exception")
                        .param("type", type))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());

    }

    @Test
    @DisplayName("程序内其他异常")
    void internalServerErrorWhenThrowOtherException() throws Exception {
        // given
        final Class<Throwable> errorType = Throwable.class;
        final String errorMessage = "服务异常";

        // when
        mockMvc.perform(get("/exception"))
                // then
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(errorType))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}