package net.wuxianjie.core.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * REST API 响应结果全局处理。
 */
@ControllerAdvice
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  private final ObjectMapper objectMapper;

  @Override
  public boolean supports(@NonNull MethodParameter returnType,
                          @NonNull Class<? extends HttpMessageConverter<?>> converterType
  ) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body,
                                @NonNull MethodParameter returnType,
                                @NonNull MediaType selectedContentType,
                                @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                @NonNull ServerHttpRequest request,
                                @NonNull ServerHttpResponse response
  ) {
    if (body instanceof String) {
      try {
        return objectMapper.writeValueAsString(RestDataWrapper.success(body));
      } catch (JsonProcessingException e) {
        throw new RuntimeException("统一响应结果处理时 JSON 序列化失败", e);
      }
    }

    if (
      body instanceof RestData ||
        body instanceof ResponseEntity ||
        body instanceof byte[]
    ) {
      return body;
    }

    return RestDataWrapper.success(body);
  }
}
