package net.wuxianjie.springbootcore.rest;

import net.wuxianjie.springbootcore.shared.CommonValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = RestApiController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ExceptionControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenAcceptHttpRequestHeaderNotSupportedButAcceptJsonShouldReturn406HttpStatusAndJsonBody()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/html")
                        .accept(MediaType.APPLICATION_JSON, MediaType.IMAGE_JPEG))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE,
                                CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"API 不支持返回请求头指定的 MIME 类型 [Accept: application/json, image/jpeg]\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenAcceptHttpRequestHeaderNotSupportedAndNotAcceptJsonShouldReturn406HttpStatusAndNoBody()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/html")
                        .accept(MediaType.IMAGE_JPEG))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.header()
                        .doesNotExist(HttpHeaders.CONTENT_TYPE))
                .andExpect(result -> Assertions.assertTrue(
                        result.getResponse().getContentAsString().isEmpty()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenAcceptAllHttpRequestHeaderShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/html")
                        .accept(MediaType.IMAGE_JPEG, MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE,
                                "text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":0," +
                        "\"errMsg\":null," +
                        "\"data\":\"<h1>Hello World</h1>\"" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenExactlyAcceptHttpRequestHeaderSupportedShouldReturnOk()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/html")
                        .accept(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE,
                                "text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":0," +
                        "\"errMsg\":null," +
                        "\"data\":\"<h1>Hello World</h1>\"" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenEmptyAcceptHttpRequestHeaderSupportedShouldReturnOk()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/html"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE,
                                "text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":0," +
                        "\"errMsg\":null," +
                        "\"data\":\"<h1>Hello World</h1>\"" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenHttpRequestMethodNotSupportedShouldReturn405HttpStatus()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/html"))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE,
                                CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"API 不支持 POST 请求方法\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenLackOfDevHttpHeaderShouldReturn404HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/header"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenExistsDevHttpHeaderButNotEqualsShouldReturn404HttpStatus()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/header")
                        .header("dev", "Jason"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenExactlyEqualsDevHttpHeaderShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/header")
                        .header("dev", "吴仙杰"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":0," +
                        "\"errMsg\":null," +
                        "\"data\":\"吴仙杰\"" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenMalformedRequestBodyShouldReturn400HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"请求体内容有误\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenLackOfRequiredRequestParameterShouldReturn400HttpStatus()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/required"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"缺少必填参数 name\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenRequestParameterInvalidShouldReturn400HttpStatusWithValidated()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validated"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> {
                    String resp = result.getResponse().getContentAsString();

                    Assertions.assertAll("返回两个字段的校验结果",
                            () -> Assertions.assertTrue(resp.contains("用户 ID 不能为 null")),
                            () -> Assertions.assertTrue(resp.contains("用户名不能为空")));
                })
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenRequestParameterInvalidShouldReturn400HttpStatusWithValid()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/valid"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> {
                    String resp = result.getResponse().getContentAsString();

                    Assertions.assertAll("返回两个字段的校验结果",
                            () -> Assertions.assertTrue(resp.contains("用户 ID 不能为 null")),
                            () -> Assertions.assertTrue(resp.contains("用户名不能为空")));
                })
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenControllerThrowsNotFoundExceptionShouldReturn404HttpStatusWarnLog()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exception")
                        .param("type", " not-found "))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"未找到指定的数据\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenControllerThrowsBadRequestExceptionShouldReturn400HttpStatusWarnLog()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exception")
                        .param("type", "bad-request"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"客户端请求错误\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenControllerThrowsConflictExceptionShouldReturn409HttpStatusWarnLog()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exception")
                        .param("type", "conflict"))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"已存在相同数据\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenControllerThrowsInternalExceptionShouldReturn500HttpStatusErrorLog()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/internal-error"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"服务内部异常\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenControllerThrowsJdbcExceptionShouldReturn500HttpStatus()
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exception")
                        .param("type", "\tdb\n"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"数据库操作异常\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenControllerThrowsExceptionShouldReturn500HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exception"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"服务异常\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }
}