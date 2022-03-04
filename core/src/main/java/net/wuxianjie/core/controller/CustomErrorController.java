package net.wuxianjie.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.dto.ResponseDto;
import net.wuxianjie.core.util.ResponseDtoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes attributes;

    /**
     * 作为纯后端 REST API 服务，不论客户端请求的是页面还是数据，都返回 JSON 字符串
     *
     * @param request 网络请求，用于访问一般的请求元数据，而不是用于实际处理请求
     * @return 包含错误信息的响应体
     */
    @ResponseBody
    @RequestMapping("/error")
    public ResponseEntity<ResponseDto<Void>> handleError(final WebRequest request) {
        final Map<String, Object> errorMap = attributes
                .getErrorAttributes(request, ErrorAttributeOptions.defaults());
        final Integer status = (Integer) errorMap.get("status");
        final String error = (String) errorMap.get("error");

        if (HttpStatus.NOT_FOUND.value() == status) {
            log.warn("全局 404 处理：{}", errorMap);
        } else {
            log.error("全局异常处理：{}", errorMap);
        }

        HttpStatus httpStatus;

        try {
            httpStatus = Objects.requireNonNull(HttpStatus.resolve(status));
        } catch (NullPointerException e) {
            log.warn("全局异常处理，无法解析 HTTP 状态码【{}】", status);

            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(ResponseDtoWrapper.fail(error), httpStatus);
    }
}
