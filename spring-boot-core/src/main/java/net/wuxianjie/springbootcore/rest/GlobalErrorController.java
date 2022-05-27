package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.security.AuthenticationUtils;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.util.NetUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
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
   * @param webRequest {@link WebRequest}
   * @return {@link ResponseEntity}
   */
  @ResponseBody
  @RequestMapping("/error")
  public ResponseEntity<ApiResult<Void>> handleError(WebRequest webRequest) {
    Map<String, Object> defaultErrorAttributes = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

    HttpStatus httpStatus = Optional.ofNullable((Integer) defaultErrorAttributes.get("status"))
      .map(c -> Optional.ofNullable(HttpStatus.resolve(c)).orElse(HttpStatus.INTERNAL_SERVER_ERROR))
      .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

    String username = AuthenticationUtils.getCurrentUser().map(TokenUserDetails::getUsername).orElse(null);
    String clientInfo = StrUtil.format("{};user={}", webRequest.getDescription(true), username);
    String responseMessage;
    if (httpStatus == HttpStatus.NOT_FOUND) {
      HttpServletRequest servletRequest = NetUtils.getRequest().orElseThrow();
      String requestMethod = servletRequest.getMethod();
      String requestUri = (String) defaultErrorAttributes.get("path");
      responseMessage = StrUtil.format("未找到 API [{} {}]", requestMethod, requestUri);
      log.warn("响应信息：{}，原始信息：{}，客户端信息：{}", responseMessage, defaultErrorAttributes, clientInfo);
    } else {
      responseMessage = "服务异常";
      log.error("响应信息：{}，原始信息：{}，客户端信息：{}", responseMessage, defaultErrorAttributes, clientInfo);
    }

    return new ResponseEntity<>(ApiResultWrapper.fail(responseMessage), httpStatus);
  }
}
