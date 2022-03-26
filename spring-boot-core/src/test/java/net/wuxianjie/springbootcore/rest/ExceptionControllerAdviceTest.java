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
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
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
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
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
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
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
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
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
        mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
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
        mockMvc.perform(MockMvcRequestBuilders.post("/hello"))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, CommonValues.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> Assertions.assertTrue(
                        result.getResponse().getContentAsString()
                                .contains("API 不支持 POST 请求方法")))
                .andDo(MockMvcResultHandlers.print());
    }
}