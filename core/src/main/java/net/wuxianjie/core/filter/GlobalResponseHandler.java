package net.wuxianjie.core.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.model.ResponseResult;
import net.wuxianjie.core.util.ResponseResultWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 实现 REST API 统一响应结果
 *
 * @author 吴仙杰
 */
@ControllerAdvice
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

  private final ObjectMapper objectMapper;

  @Override
  public boolean supports(final MethodParameter returnType, final Class<? extends HttpMessageConverter<?>> converterType) {
    return true; // 对所有控制器均有效
  }

  @Override
  public Object beforeBodyWrite(final Object body, final MethodParameter returnType, final MediaType selectedContentType, final Class<? extends HttpMessageConverter<?>> selectedConverterType, final ServerHttpRequest request, final ServerHttpResponse response) {

    // 解决控制器方法返回字符串时转换异常的问题
    if (body instanceof String) {
      try {
        return objectMapper.writeValueAsString(ResponseResultWrappers.success(body));
      } catch (JsonProcessingException e) {
        throw new RuntimeException("统一响应结果处理，JSON 序列化字符串失败", e);
      }
    }

    if (body instanceof ResponseResult || body instanceof ResponseEntity) {
      return body;
    }

    return ResponseResultWrappers.success(body);
  }
}
