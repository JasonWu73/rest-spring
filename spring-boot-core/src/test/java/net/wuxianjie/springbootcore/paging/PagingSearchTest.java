package net.wuxianjie.springbootcore.paging;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = PagingSearchController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import(
        {
                AnnotationAwareAspectJAutoProxyCreator.class,
                PagingOffsetFieldPaddingAspect.class
        }
)
class PagingSearchTest {

    private static final String PAGE_ONE_RESULT = "{" +
            "\"pageNo\":1," +
            "\"pageSize\":2," +
            "\"total\":5," +
            "\"list\":[\"One\",\"Two\"]" +
            "}";


    private static final String PAGE_TWO_RESULT = "{" +
            "\"pageNo\":2," +
            "\"pageSize\":2," +
            "\"total\":5," +
            "\"list\":[\"Three\",\"Four\"]" +
            "}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPageOneDataShouldReturnPageOneResult() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/paging")
                        .param("pageNo", "1")
                        .param("pageSize", "2")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(PAGE_ONE_RESULT))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getPageTwoDataShouldReturnPageTwoResult() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/paging")
                        .param("pageNo", "2")
                        .param("pageSize", "2")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(PAGE_TWO_RESULT));
    }

    @Test
    void whenPageNoNullShouldResponse400HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/paging")
                        .param("pageNo", (String) null)
                        .param("pageSize", "2"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenPageNoLessThanOneShouldResponse400HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/paging")
                        .param("pageNo", "0")
                        .param("pageSize", "2"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void whenPageSizeNullShouldResponse400HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/paging")
                        .param("pageNo", "1")
                        .param("pageSize", (String) null))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void whenPageSizeLessThanOneShouldResponse400HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/paging")
                        .param("pageNo", "1")
                        .param("pageSize", "0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
