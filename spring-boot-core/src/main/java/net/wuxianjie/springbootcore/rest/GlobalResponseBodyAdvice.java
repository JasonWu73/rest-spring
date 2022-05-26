package net.wuxianjie.springbootcore.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.InternalException;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * REST API 响应结果统一处理。
 *
 * @author 吴仙杰
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  private static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

  private final ObjectMapper objectMapper;

  @Override
  public boolean supports(MethodParameter returnType,
                          Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body,
                                MethodParameter returnType,
                                MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request,
                                ServerHttpResponse response) {
    if (!selectedContentType.isCompatibleWith(MediaType.APPLICATION_JSON)) return body;

    // 自动包装字符串为 JSON
    if (body instanceof String) {
      response.getHeaders().set(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
      try {
        return objectMapper.writeValueAsString(ApiResultWrapper.success(body));
      } catch (JsonProcessingException e) {
        throw new InternalException("响应结果 JSON 序列化失败", e);
      }
    }

    // REST API 响应结果
    boolean isRestApiResult = body instanceof ApiResult || body instanceof ResponseEntity;
    // 字节数组，例如用于返回浏览器可直接打开的图片
    boolean isBytes = body instanceof byte[];
    // 资源类型，例如视频点播（HTTP STATUS 206）
    boolean isResource = body instanceof ResourceRegion;

    if (isRestApiResult || isBytes || isResource) return body;

    // 其他情况一律包装为 JSON
    return ApiResultWrapper.success(body);
  }
}
