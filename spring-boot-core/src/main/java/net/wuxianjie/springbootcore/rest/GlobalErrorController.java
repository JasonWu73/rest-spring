package net.wuxianjie.springbootcore.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 全局异常处理。
 *
 * @author 吴仙杰
 * @see ExceptionControllerAdvice
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GlobalErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @ResponseBody
    @RequestMapping("/error")
    public ResponseEntity<RestData<Void>> handleError(WebRequest request) {
        Map<String, Object> errorMap = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        Integer httpStatusCode = (Integer) errorMap.get("status");
        if (httpStatusCode == HttpStatus.NOT_FOUND.value()) {
            log.warn("全局 404 处理：{}", errorMap);
        } else {
            log.error("全局异常处理（未知）：{}", errorMap);
        }

        HttpStatus httpStatus;
        try {
            httpStatus = Objects.requireNonNull(HttpStatus.resolve(httpStatusCode));
        } catch (NullPointerException e) {
            log.warn("全局异常处理 -> 无法解析 HTTP 状态码【{}】", httpStatusCode);

            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String errorMessage = (String) errorMap.get("error");
        return new ResponseEntity<>(RestDataWrapper.fail(errorMessage), httpStatus);
    }
}
