package net.wuxianjie.springbootcore.validator;

import net.wuxianjie.springbootcore.rest.ExceptionControllerAdvice;
import net.wuxianjie.springbootcore.rest.GlobalResponseBodyAdvice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = ParamController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import({ExceptionControllerAdvice.class, GlobalResponseBodyAdvice.class})
class EnumValidatorImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenLackOfSaveParamShouldReturn400HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/param/save"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"启用状态不能为 null\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenRightSaveParamShouldReturn200HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/param/save")
                        .param("enabled", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":0," +
                        "\"errMsg\":null," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenWrongSaveParamShouldReturn200HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/param/save")
                        .param("enabled", "11"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"启用状态错误\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenLackOfUpdateParamShouldReturn400HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/param/update"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":1," +
                        "\"errMsg\":\"ID 不能为 null\"," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenRightUpdateParamShouldReturn200HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/param/update")
                        .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"error\":0," +
                        "\"errMsg\":null," +
                        "\"data\":null" +
                        "}"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenEnumNotHaveValueMethodShouldReturn200HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/param/wrong")
                        .param("type", "FACEBOOK"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenEnumErrorCallValueMethodShouldReturn200HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/param/wrong-2")
                        .param("type", "FACEBOOK"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}