package net.wuxianjie.springbootcore.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author 吴仙杰
 */
@Import({
        AnnotationAwareAspectJAutoProxyCreator.class,
        OperationLogAspect.class,
        ApiService.class
})
@WebMvcTest(
        controllers = ApiController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class OperationLogAspectTest {

    @MockBean
    private OperationService logService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiService underTest;

    @Test
    @DisplayName("调用测试接口")
    void canLogGetResult() throws Exception {
        // given
        ApiController.Param param = new ApiController.Param() {{
            setName("测试外部数据");
            setData(new ApiController.ParamData() {{
                setName("测试内部数据");
                setStatus(100);
            }});
        }};

        // when
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(param)))
                .andDo(print());

        // then
        verify(logService).saveLog(isA(OperationLog.class));
    }

    @Test
    @DisplayName("调用无参无返回值方法")
    void canLogCallMethod() {
        // given
        // when
        underTest.callMethod();

        // then
        verify(logService).saveLog(isA(OperationLog.class));
    }

    @Test
    @DisplayName("调用有原始类型入参及返回 null 值方法")
    void canLogCallMethodReturnNull() {
        // given
        int i = 100;

        // when
        Integer actual = underTest.callMethodReturnNull(i);

        // then
        verify(logService).saveLog(isA(OperationLog.class));

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("调用有原始类型入参及返回值方法")
    void canLogCallPrimitiveTypeMethod() {
        // given
        int i = 100;

        // when
        int actual = underTest.callMethod(i);

        // then
        verify(logService).saveLog(isA(OperationLog.class));

        assertThat(actual).isEqualTo(i);
    }
}