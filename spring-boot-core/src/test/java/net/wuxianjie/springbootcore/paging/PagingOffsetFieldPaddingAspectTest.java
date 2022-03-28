package net.wuxianjie.springbootcore.paging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 吴仙杰
 */
@WebMvcTest(
        controllers = PagingSearchController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import(
        {
                AnnotationAwareAspectJAutoProxyCreator.class,
                PagingOffsetFieldPaddingAspect.class
        }
)
class PagingOffsetFieldPaddingAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("获取第二页数据")
    void canGetSecondPageData() throws Exception {
        mockMvc.perform(get("/paging")
                        .param("pageNo", "2")
                        .param("pageSize", "2")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                                "{" +
                                        "\"pageNo\":2," +
                                        "\"pageSize\":2," +
                                        "\"total\":5," +
                                        "\"list\":[\"Three\",\"Four\"]" +
                                        "}"
                        )
                );
    }
}
