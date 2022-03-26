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
import java.util.Optional;

/**
 * Spring Boot 全局异常处理。
 *
 * @author 吴仙杰
 * @see ExceptionControllerAdvice
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class GlobalErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    /**
     * 处理在进入 Controller 之前就抛出的异常。
     *
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @RequestMapping("/error")
    public ResponseEntity<ApiResult<Void>> handleError(WebRequest request) {
        Map<String, Object> attrToValue = errorAttributes.getErrorAttributes(
                request, ErrorAttributeOptions.defaults());

        HttpStatus httpStatus = Optional.ofNullable(
                        (Integer) attrToValue.get("status"))
                .map(code -> Optional.ofNullable(HttpStatus.resolve(code))
                        .orElse(HttpStatus.INTERNAL_SERVER_ERROR))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        if (httpStatus == HttpStatus.NOT_FOUND) {
            log.warn("Spring Boot 全局 404 处理：{}", attrToValue);
        } else {
            log.error("Spring Boot 全局异常处理：{}", attrToValue);
        }

        return new ResponseEntity<>(
                ApiResultWrapper.fail((String) attrToValue.get("error")),
                httpStatus);
    }
}
