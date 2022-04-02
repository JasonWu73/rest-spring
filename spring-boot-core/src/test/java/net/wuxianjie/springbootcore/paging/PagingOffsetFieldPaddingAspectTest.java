package net.wuxianjie.springbootcore.paging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = PagingTestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import({AnnotationAwareAspectJAutoProxyCreator.class, PagingOffsetFieldPaddingAspect.class})
class PagingOffsetFieldPaddingAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("获取第二页数据")
    void canGetSecondPageData() throws Exception {
        //given
        final String pageNo = "2";
        final String pageSize = "2";

        // when
        mockMvc.perform(get("/paging")
                        .param("pageNo", pageNo)
                        .param("pageSize", pageSize))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNo").value(pageNo))
                .andExpect(jsonPath("$.pageSize").value(pageSize))
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.list[0]").value("Three"))
                .andExpect(jsonPath("$.list[1]").value("Four"));
    }


    @Test
    @DisplayName("无法获取分页数据 - 分页参数错误")
    void canNotGetPageDataWhenPagingInvalidParameter() throws Exception {
        //given
        final String pageNo = "0";
        final String pageSize = "0";

        // when
        mockMvc.perform(get("/paging")
                        .param("pageNo", pageNo)
                        .param("pageSize", pageSize))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(BindException.class)
                        .hasMessageContainingAll(
                                "页码不能小于 1",
                                "每页条数不能小于 1"
                        ));
    }
}
