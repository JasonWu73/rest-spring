package net.wuxianjie.springbootcore.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * @author 吴仙杰
 */
@Import({AnnotationAwareAspectJAutoProxyCreator.class, OperationLogAspect.class, ApiTestService.class})
@WebMvcTest(controllers = ApiTestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class OperationLogAspectTest {

    @MockBean
    private OperationService logService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApiTestService underTest;

    @Test
    @DisplayName("调用测试接口")
    void canLogGetResult() throws Exception {
        // given
        ApiTestController.Param param = new ApiTestController.Param() {{
            setName("测试外部数据");
            setData(new ApiTestController.ParamData() {{
                setName("测试内部数据");
                setStatus(100);
            }});
        }};

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(param)))
                .andDo(MockMvcResultHandlers.print());

        // then
        Mockito.verify(logService).saveLog(ArgumentMatchers.isA(OperationLog.class));
    }

    @Test
    @DisplayName("调用无参无返回值方法")
    void canLogCallMethod() {
        // given
        // when
        underTest.callMethod();

        // then
        Mockito.verify(logService).saveLog(ArgumentMatchers.isA(OperationLog.class));
    }

    @Test
    @DisplayName("调用有原始类型入参及返回 null 值方法")
    void canLogCallPrimitiveArgumentMethodReturnNull() {
        // given
        int i = 100;

        // when
        Integer actual = underTest.callMethodReturnNull(i);

        // then
        Mockito.verify(logService).saveLog(ArgumentMatchers.isA(OperationLog.class));
        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("调用有原始类型入参及返回原始类型值方法")
    void canLogCallPrimitiveArgumentMethodAndReturnPrimitive() {
        // given
        int i = 100;

        // when
        int actual = underTest.callMethod(i);

        // then
        Mockito.verify(logService).saveLog(ArgumentMatchers.isA(OperationLog.class));
        Assertions.assertThat(actual).isEqualTo(i);
    }
}