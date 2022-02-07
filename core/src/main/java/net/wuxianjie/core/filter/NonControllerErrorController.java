package net.wuxianjie.core.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.model.dto.RestDto;
import net.wuxianjie.core.constant.Mappings;
import net.wuxianjie.core.util.ResponseResultWrappers;
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
 * 定制 Spring Boot 白标签错误页 (Whitelabel Error Page)
 *
 * <p>{@code @ControllerAdvice} 与 {@code @ExceptionHandler} 配合, 只能处理控制器抛出的异常, 此时请求已进入控制器</p>
 *
 * <p>实现 {@code ErrorController} 接口, 则可以处理所有异常, 包括未进入控制器的错误, 比如404</p>
 *
 * <p>如果 {@code @ControllerAdvice} 与 {@code ErrorController} 实现类同时存在,
 * 则 {@code @ControllerAdvice} 方式处理控制器抛出的异常,
 * {@code ErrorController} 实现类处理未进入控制器的异常</p>
 *
 * @author 吴仙杰
 * @see <a href="https://www.baeldung.com/spring-boot-custom-error-page">Spring Boot: Customize Whitelabel Error Page | Baeldung</a>
 */
@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NonControllerErrorController implements ErrorController {

  /** Spring Boot 记录下来的错误属性 */
  private final ErrorAttributes attributes;

  /**
   * 作为纯后端 REST API 服务, 不论客户端请求的是页面还是数据, 都返回 JSON 字符串
   *
   * @param request 网络请求, 用于访问一般的请求元数据, 而不是用于实际处理请求
   * @return 包含错误信息的响应体
   */
  @ResponseBody
  @RequestMapping(Mappings.ERROR)
  public ResponseEntity<RestDto<Void>> handleError(final WebRequest request) {

    final Map<String, Object> errorMap = attributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

    final Integer status = (Integer) errorMap.get("status");
    final String error = (String) errorMap.get("error");

    if (status == HttpStatus.NOT_FOUND.value()) {
      log.warn("全局404处理: {}", errorMap);
    } else {
      log.error("全局异常处理: {}", errorMap);
    }

    HttpStatus httpStatus;
    try {
       httpStatus = Objects.requireNonNull(HttpStatus.resolve(status));
    } catch (NullPointerException e) {
      log.warn("NonControllerErrorController 全局异常处理，无法解析 {} HTTP 状态码", status);

      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    return new ResponseEntity<>(ResponseResultWrappers.fail(error), httpStatus);
  }
}
