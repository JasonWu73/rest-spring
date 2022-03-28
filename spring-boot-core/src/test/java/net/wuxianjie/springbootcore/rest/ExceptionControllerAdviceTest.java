package net.wuxianjie.springbootcore.rest;

import net.wuxianjie.springbootcore.shared.CommonValues;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@WebMvcTest(
        controllers = RestApiController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class ExceptionControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("请求头 Accept 包含 JSON 但不包含目标 API 返回的 MIME 类型")
    void itShouldCheckWhenAcceptRequestHeaderContainJsonButApiNotSupported()
            throws Exception {
        mockMvc.perform(get("/html")
                        .accept(
                                MediaType.APPLICATION_JSON,
                                MediaType.APPLICATION_XML
                        )
                )
                .andExpect(status().isNotAcceptable())
                .andExpect(header().string(
                                HttpHeaders.CONTENT_TYPE,
                                CommonValues.APPLICATION_JSON_UTF8_VALUE
                        )
                )
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"API 不支持返回请求头指定的 MIME 类型 [Accept: application/json, application/xml]\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("请求头 Accept 既不包含 JSON 也不包含目标 API 返回的 MIME 类型")
    void itShouldCheckWhenAcceptRequestHeaderNotContainJsonAndApiNotSupported()
            throws Exception {
        mockMvc.perform(get("/html")
                        .accept(MediaType.APPLICATION_XML)
                )
                .andExpect(status().isNotAcceptable())
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_TYPE))
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString())
                                .isEmpty()
                );
    }

    @Test
    @DisplayName("目标 API 不支持请求方法")
    void itShouldCheckWhenRequestMethodApiNotSupported() throws Exception {
        mockMvc.perform(post("/html"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string(
                                HttpHeaders.CONTENT_TYPE,
                                CommonValues.APPLICATION_JSON_UTF8_VALUE
                        )
                )
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"API 不支持 POST 请求方法\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("请求体内容格式错误")
    void itShouldCheckWhenRequestBodyMalformed() throws Exception {
        mockMvc.perform(post("/body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"请求体内容有误\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("请求缺少必填参数")
    void itShouldCheckWhenRequestLackOfRequiredParam() throws Exception {
        mockMvc.perform(get("/required"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"缺少必填参数 name\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("请求参数校验不合法 @Validated")
    void itShouldCheckWhenRequestParamInvalidByValidatedAnnotation() throws Exception {
        mockMvc.perform(get("/validated"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                            String body = result.getResponse().getContentAsString();

                            assertThat(body)
                                    .contains("用户 ID 不能为 null")
                                    .contains("用户名不能为空");
                        }
                );
    }

    @Test
    @DisplayName("请求参数校验不合法 @Valid")
    void itShouldCheckWhenRequestParamInvalidByValidAnnotation() throws Exception {
        mockMvc.perform(get("/valid"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                            String body = result.getResponse().getContentAsString();

                            assertThat(body)
                                    .contains("用户 ID 不能为 null")
                                    .contains("用户名不能为空");
                        }
                );
    }

    @Test
    @DisplayName("当程序抛出 NotFoundException")
    void itShouldCheckWhenThrowNotFoundException() throws Exception {
        mockMvc.perform(get("/exception")
                        .param("type", " not_found ")
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"未找到指定的数据\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("当程序抛出 BadRequestException")
    void itShouldCheckWhenThrowBadRequestException() throws Exception {
        mockMvc.perform(get("/exception")
                        .param("type", "bad_request")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"客户端请求错误\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("当程序抛出 ConflictException")
    void itShouldCheckWhenThrowConflictException() throws Exception {
        mockMvc.perform(get("/exception")
                        .param("type", "conflict")
                )
                .andExpect(status().isConflict())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"已存在相同数据\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("当程序抛出 InternalException")
    void itShouldCheckWhenThrowInternalException() throws Exception {
        mockMvc.perform(get("/exception")
                        .param("type", "internal")
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"服务内部异常\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("当程序抛出 JDBC 异常")
    void itShouldCheckWhenThrowJdbcException() throws Exception {
        mockMvc.perform(get("/exception")
                        .param("type", "\tdb\n")
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"数据库操作异常\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("当程序抛出非自定义异常")
    void itShouldCheckWhenThrowException() throws Exception {
        mockMvc.perform(get("/exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"服务异常\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }
}