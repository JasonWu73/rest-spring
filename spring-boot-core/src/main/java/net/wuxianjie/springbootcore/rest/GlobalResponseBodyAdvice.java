package net.wuxianjie.springbootcore.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.shared.CommonValues;
import net.wuxianjie.springbootcore.shared.InternalException;
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

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(
            @NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {
        // 自动包装字符串为 JSON
        if (body instanceof String) {
            try {
                response.getHeaders()
                        .set(
                                HttpHeaders.CONTENT_TYPE,
                                CommonValues.APPLICATION_JSON_UTF8_VALUE
                        );

                return objectMapper.writeValueAsString(ApiResultWrapper.success(body));
            } catch (JsonProcessingException e) {
                throw new InternalException("响应结果 JSON 序列化失败", e);
            }
        }

        // 若已经是响应对象，则直接返回
        if (body instanceof ApiResult // 自定义统一 REST API 响应对象
                || body instanceof ResponseEntity // Spring 自带的响应对象
                || body instanceof byte[] // 如图片
                || body instanceof ResourceRegion // 如视频点播（HTTP STATUS 206）
        ) {
            return body;
        }

        // 其他情况一律包装为 JSON
        return ApiResultWrapper.success(body);
    }
}
