package net.wuxianjie.springbootcore.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
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
@Slf4j
@Controller
@RequiredArgsConstructor
public class GlobalErrorController implements ErrorController {

  private final ErrorAttributes errorAttributes;

  /**
   * 处理在进入 Controller 之前就抛出的异常。
   *
   * @param req {@link WebRequest}
   * @return {@link ResponseEntity}
   */
  @ResponseBody
  @RequestMapping("/error")
  public ResponseEntity<ApiResult<Void>> handleError(WebRequest req) {
    Map<String, Object> attributes = errorAttributes.getErrorAttributes(req, ErrorAttributeOptions.defaults());

    HttpStatus httpStatus = Optional.ofNullable((Integer) attributes.get("status"))
      .map(code -> Optional.ofNullable(HttpStatus.resolve(code))
        .orElse(HttpStatus.INTERNAL_SERVER_ERROR))
      .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

    Optional<TokenUserDetails> userOpt = AuthUtils.getCurrentUser();
    Integer accountId = userOpt.map(TokenUserDetails::getUserId).orElse(null);
    String accountName = userOpt.map(TokenUserDetails::getUsername).orElse(null);

    String reqDes = req.getDescription(true).replaceAll(";", "；");

    if (httpStatus == HttpStatus.NOT_FOUND) {
      log.warn(
        "{}；accountName={}；accountId={} - Spring Boot 全局 404 处理：{}",
        reqDes,
        accountName,
        accountId,
        attributes
      );
    } else {
      log.error(
        "{}；accountName={}；accountId={} - Spring Boot 全局异常处理：{}",
        reqDes,
        accountName,
        accountId,
        attributes
      );
    }

    String error = (String) attributes.get("error");

    return new ResponseEntity<>(ApiResultWrapper.fail(error), httpStatus);
  }
}
