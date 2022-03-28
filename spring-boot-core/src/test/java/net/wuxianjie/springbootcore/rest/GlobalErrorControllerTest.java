package net.wuxianjie.springbootcore.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.RequestDispatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 吴仙杰
 */
@WebMvcTest(
        controllers = RestApiController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import(GlobalErrorController.class)
class GlobalErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Spring Boot 白标签错误页 404")
    void itShouldCheckWhen404WhiteLabelErrorPage() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(
                                RequestDispatcher.ERROR_STATUS_CODE,
                                HttpStatus.NOT_FOUND.value()
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"Not Found\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }

    @Test
    @DisplayName("Spring Boot 白标签错误页 500")
    void itShouldCheckWhen500WhiteLabelErrorPage() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(
                                "{" +
                                        "\"error\":1," +
                                        "\"errMsg\":\"None\"," +
                                        "\"data\":null" +
                                        "}"
                        )
                );
    }
}