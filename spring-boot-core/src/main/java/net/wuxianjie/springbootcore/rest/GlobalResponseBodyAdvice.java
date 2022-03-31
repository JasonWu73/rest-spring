package net.wuxianjie.springbootcore.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.shared.exception.InternalException;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
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

    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(final @NonNull MethodParameter returnType,
                            final @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(final Object body,
                                  final @NonNull MethodParameter returnType,
                                  final @NonNull MediaType selectedContentType,
                                  final @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  final @NonNull ServerHttpRequest request,
                                  final @NonNull ServerHttpResponse response) {
        // 自动包装字符串为 JSON
        if (body instanceof String) {
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
            try {
                return objectMapper.writeValueAsString(ApiResultWrapper.success(body));
            } catch (JsonProcessingException e) {
                throw new InternalException("响应结果 JSON 序列化失败", e);
            }
        }

        // 若已经是可用的响应对象，则直接返回
        if (body instanceof ApiResult
                || body instanceof ResponseEntity
                // 字节数组，如用于返回浏览器可直接打开的图片
                || body instanceof byte[]
                // 资源类型，如视频点播（HTTP STATUS 206）
                || body instanceof ResourceRegion) {
            return body;
        }

        // 其他情况一律包装为 JSON
        return ApiResultWrapper.success(body);
    }
}
