package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.exception.AbstractBaseException;
import net.wuxianjie.springbootcore.exception.AbstractServerBaseException;
import net.wuxianjie.springbootcore.security.AuthenticationUtils;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.util.NetUtils;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller 层全局异常处理。
 *
 * @author 吴仙杰
 * @see GlobalErrorController
 */
@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

  /**
   * 处理非正常关闭 socket 引发的错误。
   *
   * @param e {@link ClientAbortException}
   */
  @ExceptionHandler(ClientAbortException.class)
  public void handleException(ClientAbortException e) {
    logWarn("非正常关闭 socket", e);
  }

  /**
   * 处理因 HTTP 请求的 MIME 类型与目标 API 不匹配而导致的异常。
   *
   * @param e       {@link HttpMediaTypeNotAcceptableException}
   * @param request {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<ApiResult<Void>> handleException(HttpMediaTypeNotAcceptableException e,
                                                         HttpServletRequest request) {
    String mimeTypes = Optional.ofNullable(request.getHeaders(HttpHeaders.ACCEPT))
      .map(en -> {
        List<String> accepts = new ArrayList<>();
        while (en.hasMoreElements()) {
          accepts.add(en.nextElement().strip());
        }

        return String.join(",", accepts);
      })
      .orElse("null");

    String responseMessage = StrUtil.format("不支持 MIME [{}: {}]", HttpHeaders.ACCEPT, mimeTypes);
    logWarn(responseMessage, e);

    return createResponseEntity(HttpStatus.NOT_ACCEPTABLE, responseMessage);
  }

  /**
   * 处理因 HTTP 请求的请求方法与目标 API 要求的请求方法不匹配而导致的异常。
   *
   * @param e       {@link HttpRequestMethodNotSupportedException}
   * @param request {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResult<Void>> handleException(HttpRequestMethodNotSupportedException e,
                                                         HttpServletRequest request) {
    String responseMessage = StrUtil.format("不支持 {} 请求", request.getMethod());
    logWarn(responseMessage, e);

    return createResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, responseMessage);
  }

  /**
   * 处理因 HTTP 请求不是 Multipart Request，但 Controller 中参数存在 {@link MultipartFile} 而导致的异常。
   *
   * @param e {@link MultipartException}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<ApiResult<Void>> handleException(MultipartException e) {
    String responseMessage = "仅支持 Multipart 请求";
    logWarn(responseMessage, e);

    return createResponseEntity(HttpStatus.BAD_REQUEST, responseMessage);
  }

  /**
   * 处理因无法解析 HTTP 请求体内容而导致的异常。
   *
   * @param e {@link HttpMessageNotReadableException}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResult<Void>> handleException(HttpMessageNotReadableException e) {
    String responseMessage = "请求体内容不合法";
    logWarn(responseMessage, e);

    return createResponseEntity(HttpStatus.BAD_REQUEST, responseMessage);
  }

  /**
   * 处理因 HTTP 请求缺少必填参数而导致的异常。
   *
   * @param e {@link MissingServletRequestParameterException}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResult<Void>> handleException(MissingServletRequestParameterException e) {
    String responseMessage = StrUtil.format("缺少必填参数 [{}]", e.getParameterName());
    logWarn(responseMessage, e);

    return createResponseEntity(HttpStatus.BAD_REQUEST, responseMessage);
  }

  /**
   * 处理因 HTTP 请求缺少必传文件而导致的异常。
   *
   * @param e {@link MissingServletRequestPartException}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<ApiResult<Void>> handleException(MissingServletRequestPartException e) {
    String responseMessage = StrUtil.format("缺少必传文件，请检查参数 [{}]", e.getRequestPartName());
    logWarn(responseMessage, e);

    return createResponseEntity(HttpStatus.BAD_REQUEST, responseMessage);
  }

  /**
   * 处理因 HTTP 请求参数校验 [{@link Validated}] 不通过而导致的异常。
   *
   * @param e {@link ConstraintViolationException}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResult<Void>> handleException(ConstraintViolationException e) {
    List<String> responseMessages = new ArrayList<>();

    Optional.ofNullable(e.getConstraintViolations())
      .ifPresent(vs -> vs.forEach(v -> responseMessages.add(v.getMessage())));

    String responseMessage = String.join("；", responseMessages);
    logWarn(responseMessage, e);

    return createResponseEntity(HttpStatus.BAD_REQUEST, responseMessage);
  }

  /**
   * 处理因 HTTP 请求参数校验 [{@link Valid}] 不通过而导致的异常。
   *
   * @param e {@link BindException}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiResult<Void>> handleException(BindException e) {
    List<String> responseMessages = new ArrayList<>();

    e.getBindingResult().getFieldErrors()
      .forEach(fe -> {
        // 当 Controller 接收的参数类型不符合要求时，只需提示参数有误即可，而不是返回服务异常
        boolean isControllerArgTypeError = fe.contains(TypeMismatchException.class);
        String message = isControllerArgTypeError
          ? StrUtil.format("参数类型不匹配，请检查参数 [{}]", fe.getField())
          : fe.getDefaultMessage();
        responseMessages.add(message);
      });

    String responseMessage = String.join("；", responseMessages);
    logWarn(responseMessage, e);

    return createResponseEntity(HttpStatus.BAD_REQUEST, responseMessage);
  }

  /**
   * 处理 JDBC 操作异常。
   *
   * @param e {@link UncategorizedDataAccessException}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(UncategorizedDataAccessException.class)
  public ResponseEntity<ApiResult<Void>> handleException(UncategorizedDataAccessException e) {
    String responseMessage = "数据库操作异常";
    logError(responseMessage, e);

    return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, responseMessage);
  }

  /**
   * 处理自定义异常，且不记录异常堆栈信息。
   *
   * @param e {@link AbstractBaseException}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(AbstractBaseException.class)
  public ResponseEntity<ApiResult<Void>> handleCustomException(AbstractBaseException e) {
    String responseMessage = e.getMessage();
    boolean isServerError = e instanceof AbstractServerBaseException;
    if (isServerError) {
      logError(responseMessage, e);
    } else {
      logWarn(responseMessage, e.getCause());
    }

    return createResponseEntity(e.getHttpStatus(), responseMessage);
  }

  /**
   * 处理所有未被特定 {@code handleException(...)} 方法捕获的异常。
   *
   * @param e {@link Throwable}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ApiResult<Void>> handleAllException(Throwable e) {
    // 不要处理 AccessDeniedException，否则会导致 Spring Security 无法处理 403
    boolean isSpringSecurity403Exception = e instanceof AccessDeniedException;
    if (isSpringSecurity403Exception) throw (AccessDeniedException) e;

    String responseMessage = "服务异常";
    logError(responseMessage, e);

    return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, responseMessage);
  }

  private void logWarn(String responseMessage, Throwable e) {
    log.warn("响应信息：{}，原始信息：{}，客户端信息：{}", responseMessage, e == null ? "null" : e.getMessage(), getClientInfo());
  }

  private void logError(String responseMessage, Throwable e) {
    log.error("响应信息：{}，客户端信息：{}", responseMessage, getClientInfo(), e);
  }

  private String getClientInfo() {
    HttpServletRequest request = NetUtils.getRequest().orElseThrow();
    String username = AuthenticationUtils.getCurrentUser().map(TokenUserDetails::getUsername).orElse(null);
    return StrUtil.format("uri={};client={};user={}", request.getRequestURI(), NetUtils.getRealIpAddress(request), username);
  }

  private ResponseEntity<ApiResult<Void>> createResponseEntity(HttpStatus httpStatus, String responseMessage) {
    if (isJsonRequest()) return new ResponseEntity<>(ApiResultWrapper.fail(responseMessage), httpStatus);

    return new ResponseEntity<>(null, httpStatus);
  }

  private boolean isJsonRequest() {
    HttpServletRequest request = NetUtils.getRequest().orElseThrow();
    // 只有在 Accept 请求头中明确指定不包含 JSON 时才认为非 JSON 请求
    return Optional.ofNullable(request.getHeaders(HttpHeaders.ACCEPT))
      .map(e -> {
        if (!e.hasMoreElements()) return true;

        while (e.hasMoreElements()) {
          String accept = e.nextElement().strip();
          String[] jsonMimes = {MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE};
          boolean containsJson = StrUtil.containsAnyIgnoreCase(accept, jsonMimes);
          if (containsJson) return true;
        }

        return false;
      })
      .orElse(true);
  }
}
