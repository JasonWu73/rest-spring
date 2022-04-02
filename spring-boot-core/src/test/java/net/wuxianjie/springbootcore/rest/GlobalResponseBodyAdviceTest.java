package net.wuxianjie.springbootcore.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static net.wuxianjie.springbootcore.rest.GlobalResponseBodyAdvice.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = ApiTestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class GlobalResponseBodyAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Controller 返回值为 Void")
    void itShouldCheckWhenVoidReturnType() throws Exception {
        // given
        // when
        mockMvc.perform(get("/void"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("返回 null 值 - Object 返回值类型")
    void itShouldCheckWhenObjectReturnTypeReturnNull() throws Exception {
        // given
        // when
        mockMvc.perform(get("/null-when-object-return-type"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.errMsg").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("返回 null 值 - String 返回值类型")
    void itShouldCheckWhenStringReturnTypeReturnNull() throws Exception {
        // given
        // when
        mockMvc.perform(get("/null-when-string-return-type"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Controller 返回 String 值")
    void itShouldCheckWhenReturnString() throws Exception {
        // given
        final String responseContentType = "text/plain;charset=UTF-8";
        final String response = "Hello World";

        // when
        mockMvc.perform(get("/str"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(responseContentType))
                .andExpect(jsonPath("$").value(response));
    }

    @Test
    @DisplayName("Controller 返回 HTML")
    void itShouldCheckWhenReturnHtml() throws Exception {
        // given
        final String responseContentType = "text/html;charset=UTF-8";
        final String html = "<h1>Hello World</h1>";

        // when
        mockMvc.perform(get("/html"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(responseContentType))
                .andExpect(jsonPath("$").value(html));
    }

    @Test
    @DisplayName("Controller 返回字节数组")
    void itShouldCheckWhenReturnBytes() throws Exception {
        // given
        // when
        mockMvc.perform(get("/bytes"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(jsonPath("$").value("Hello Bytes"));
    }
}