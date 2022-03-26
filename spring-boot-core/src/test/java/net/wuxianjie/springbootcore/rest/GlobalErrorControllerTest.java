package net.wuxianjie.springbootcore.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.RequestDispatcher;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = RestApiController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(GlobalErrorController.class)
class GlobalErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void when404WhiteLabelErrorPageShouldReturn404HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"Not Found\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenErrorWhiteLabelErrorPageShouldReturn500HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/error"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"None\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }
}