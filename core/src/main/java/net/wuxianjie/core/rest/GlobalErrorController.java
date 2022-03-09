package net.wuxianjie.core.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * 全局处理未经过 Controller 层的异常。
 */
@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GlobalErrorController implements ErrorController {

    private final ErrorAttributes attributes;

    @ResponseBody
    @RequestMapping("/error")
    public ResponseEntity<RestData<Void>> handleError(WebRequest request) {
        final Map<String, Object> errorMap = attributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        final Integer status = (Integer) errorMap.get("status");
        final String error = (String) errorMap.get("error");

        if (HttpStatus.NOT_FOUND.value() == status) {
            log.warn("全局 404 处理：{}", errorMap);
        } else {
            log.error("全局异常处理（未知）：{}", errorMap);
        }

        HttpStatus httpStatus;

        try {
            httpStatus = Objects.requireNonNull(HttpStatus.resolve(status));
        } catch (NullPointerException e) {
            log.warn("全局异常处理发生异常：无法解析 HTTP 状态码【{}】", status);

            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(RestDataWrapper.fail(error), httpStatus);
    }
}
