package net.wuxianjie.springbootcore.operationlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.wuxianjie.springbootcore.operationlog.ApiTestController.Param;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author 吴仙杰
 */
@Import({AnnotationAwareAspectJAutoProxyCreator.class, OperationLogAspect.class})
@WebMvcTest(controllers = ApiTestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class OperationLogAspectTest {

    @MockBean
    private OperationLogService logService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiTestController underTest;

    @Test
    @DisplayName("请求 HTTP API")
    void itShouldCheckWhenRequestRestHttpApi() throws Exception {
        // given
        final Param param = new Param() {{
            setName("测试数据 Outer");
            setData(new ApiTestController.ParamData() {{
                setName("测试数据 Inner");
                setStatus(100);
            }});
        }};

        // when
        mockMvc.perform(post("/test")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)));

        // then
        verify(logService).saveLog(isA(OperationLogData.class));
    }

    @Test
    @DisplayName("调用无参无返回值方法")
    void itShouldCheckWhenCallVoidReturnMethod() {
        // given
        // when
        underTest.callMethod();

        // then
        verify(logService).saveLog(isA(OperationLogData.class));
    }

    @Test
    @DisplayName("调用有原始类型入参及返回 null 值方法")
    void itShouldCheckWhenCallReturnNullMethod() {
        // given
        final int i = 100;

        // when
        final Integer actual = underTest.callMethodReturnNull(i);

        // then
        verify(logService).saveLog(isA(OperationLogData.class));
        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("调用有原始类型入参及返回原始类型值方法")
    void itShouldCheckWhenCallReturnPrimitiveMethod() {
        // given
        final int i = 100;

        // when
        final int actual = underTest.callMethod(i);

        // then
        verify(logService).saveLog(isA(OperationLogData.class));
        Assertions.assertThat(actual).isEqualTo(i);
    }
}