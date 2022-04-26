package net.wuxianjie.springbootcore.operationlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.wuxianjie.springbootcore.operationlog.ApiTestController.InnerParameter;
import net.wuxianjie.springbootcore.operationlog.ApiTestController.OuterParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static net.wuxianjie.springbootcore.operationlog.OperationLogAspect.VOID_RETURN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author 吴仙杰
 */
@WebMvcTest(
  controllers = ApiTestController.class,
  excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import({AnnotationAwareAspectJAutoProxyCreator.class, OperationLogAspect.class})
class OperationLogAspectTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ApiTestController controller;

  @MockBean
  private OperationLogService underTest;

  @Test
  @DisplayName("请求 HTTP API")
  void canLogHttpApiRequest() throws Exception {
    // given
    OuterParameter param = new OuterParameter() {{
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
    ArgumentCaptor<OperationLogData> logDataArg = ArgumentCaptor.forClass(OperationLogData.class);
    verify(underTest).saveLog(logDataArg.capture());

    OperationLogData actual = logDataArg.getValue();

    assertThat(actual.getOperatorId()).isNull();
    assertThat(actual.getOperatorName()).isNull();
    assertThat(actual.getOperationTime()).isEqualToIgnoringNanos(LocalDateTime.now());
    assertThat(actual.getRequestIp()).isEqualTo("127.0.0.1");
    assertThat(actual.getRequestUri()).isEqualTo("/test");
    assertThat(actual.getMethodName()).isEqualTo("net.wuxianjie.springbootcore.operationlog.ApiTestController.getResult");
    assertThat(actual.getMethodMessage()).isEqualTo("测试 API");
    assertThat(actual.getParamJson()).isEqualTo("{\"param\":{\"name\":\"测试数据 Outer\",\"data\":{\"name\":\"测试数据 Inner\",\"status\":100}}}");
    assertThat(actual.getReturnJson()).isEqualTo("{\"error\":0,\"message\":\"成功\",\"data\":{\"list\":[\"One\",\"Two\"],\"size\":2}}");

  }

  @Test
  @DisplayName("入参与返回值都是 int 的方法")
  void canLogPrimitiveMethod() {
    // given
    int i = 100;

    // when
    controller.callMethod(i);

    // then
    ArgumentCaptor<OperationLogData> logDataArg = ArgumentCaptor.forClass(OperationLogData.class);
    verify(underTest).saveLog(logDataArg.capture());

    OperationLogData actual = logDataArg.getValue();

    assertThat(actual.getOperatorId()).isNull();
    assertThat(actual.getOperatorName()).isNull();
    assertThat(actual.getOperationTime()).isEqualToIgnoringNanos(LocalDateTime.now());
    assertThat(actual.getRequestIp()).isNull();
    assertThat(actual.getRequestUri()).isNull();
    assertThat(actual.getMethodName()).isEqualTo("net.wuxianjie.springbootcore.operationlog.ApiTestController.callMethod");
    assertThat(actual.getMethodMessage()).isEqualTo("入参与返回值都是 int 的方法");
    assertThat(actual.getParamJson()).isEqualTo("{\"i\":100}");
    assertThat(actual.getReturnJson()).isEqualTo("100");
  }

  @Test
  @DisplayName("入参为 int，返回值为 null 值的方法")
  void canLogReturnNullMethod() {
    // given
    int i = 100;

    // when
    controller.callMethodReturnNull(i);

    // then
    ArgumentCaptor<OperationLogData> logDataArg = ArgumentCaptor.forClass(OperationLogData.class);
    verify(underTest).saveLog(logDataArg.capture());

    OperationLogData actual = logDataArg.getValue();

    assertThat(actual.getOperatorId()).isNull();
    assertThat(actual.getOperatorName()).isNull();
    assertThat(actual.getOperationTime()).isEqualToIgnoringNanos(LocalDateTime.now());
    assertThat(actual.getRequestIp()).isNull();
    assertThat(actual.getRequestUri()).isNull();
    assertThat(actual.getMethodName()).isEqualTo("net.wuxianjie.springbootcore.operationlog.ApiTestController.callMethodReturnNull");
    assertThat(actual.getMethodMessage()).isEqualTo("入参为 int，返回值为 null 值的方法");
    assertThat(actual.getParamJson()).isEqualTo("{\"i\":100}");
    assertThat(actual.getReturnJson()).isEqualTo("null");
  }

  @Test
  @DisplayName("无参无返回值的方法")
  void canLogVoidReturnTypeMethod() {
    // given
    // when
    controller.callMethod();

    // then
    ArgumentCaptor<OperationLogData> logDataArg = ArgumentCaptor.forClass(OperationLogData.class);
    verify(underTest).saveLog(logDataArg.capture());

    OperationLogData actual = logDataArg.getValue();

    assertThat(actual.getOperatorId()).isNull();
    assertThat(actual.getOperatorName()).isNull();
    assertThat(actual.getOperationTime()).isEqualToIgnoringNanos(LocalDateTime.now());
    assertThat(actual.getRequestIp()).isNull();
    assertThat(actual.getRequestUri()).isNull();
    assertThat(actual.getMethodName()).isEqualTo("net.wuxianjie.springbootcore.operationlog.ApiTestController.callMethod");
    assertThat(actual.getMethodMessage()).isEqualTo("无参无返回值的方法");
    assertThat(actual.getParamJson()).isEqualTo("{}");
    assertThat(actual.getReturnJson()).isEqualTo(VOID_RETURN_TYPE);
  }
}