package net.wuxianjie.springbootcore.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.shared.AuthUtils;
import net.wuxianjie.springbootcore.shared.TokenUserDetails;
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
    public ResponseEntity<ApiResult<Void>> handleError(final WebRequest request) {
        final Map<String, Object> attributes = errorAttributes
                .getErrorAttributes(request, ErrorAttributeOptions.defaults());

        final HttpStatus httpStatus = Optional.ofNullable((Integer) attributes.get("status"))
                .map(code -> Optional.ofNullable(HttpStatus.resolve(code)).orElse(HttpStatus.INTERNAL_SERVER_ERROR))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        Optional<TokenUserDetails> userDetailsOptional = AuthUtils.getCurrentUser();
        final Integer accountId = userDetailsOptional.map(TokenUserDetails::getAccountId).orElse(null);
        final String accountName = userDetailsOptional.map(TokenUserDetails::getAccountName).orElse(null);

        final String requestDescription = request.getDescription(true)
                .replaceAll(";", "；");
        if (httpStatus == HttpStatus.NOT_FOUND) {
            log.warn("{}；accountName={}；accountId={} - Spring Boot 全局 404 处理：{}",
                    requestDescription, accountName, accountId, attributes);
        } else {
            log.error("{}；accountName={}；accountId={} - Spring Boot 全局异常处理：{}",
                    requestDescription, accountName, accountId, attributes);
        }

        final String error = (String) attributes.get("error");
        return new ResponseEntity<>(ApiResultWrapper.fail(error), httpStatus);
    }
}
