package net.wuxianjie.springbootcore.operationlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.wuxianjie.springbootcore.operationlog.ApiTestController.InnerParameter;
import net.wuxianjie.springbootcore.operationlog.ApiTestController.OuterParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = ApiTestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import({AnnotationAwareAspectJAutoProxyCreator.class, OperationLogAspect.class})
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
    void canLogHttpApiRequest() throws Exception {
        // given
        final OuterParameter param = new OuterParameter() {{
            setName("测试数据 Outer");
            setData(new InnerParameter() {{
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
    @DisplayName("入参与返回值都是 int 的方法")
    void canLogPrimitiveMethod() {
        // given
        final int i = 100;

        // when
        final int actual = underTest.callMethod(i);

        // then
        verify(logService).saveLog(isA(OperationLogData.class));
        assertThat(actual).isEqualTo(i);
    }

    @Test
    @DisplayName("入参为 int，返回值为 null 值的方法")
    void canLogReturnNullMethod() {
        // given
        final int i = 100;

        // when
        final Integer actual = underTest.callMethodReturnNull(i);

        // then
        verify(logService).saveLog(isA(OperationLogData.class));
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("无参无返回值的方法")
    void canLogVoidReturnTypeMethod() {
        // given
        // when
        underTest.callMethod();

        // then
        verify(logService).saveLog(isA(OperationLogData.class));
    }
}